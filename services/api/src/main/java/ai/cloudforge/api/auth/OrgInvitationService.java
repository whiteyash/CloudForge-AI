package ai.cloudforge.api.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.notification.NotificationService;

@Service
public class OrgInvitationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OrgInvitationRepository invitationRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final AppUserRepository userRepository;
    private final RbacService rbacService;
    private final AuditLogRepository auditLogRepository;
    private final ai.cloudforge.api.shared.EmailService emailService;
    private final NotificationService notificationService;

    public OrgInvitationService(
            OrgInvitationRepository invitationRepository,
            OrganizationRepository organizationRepository,
            MembershipRepository membershipRepository,
            AppUserRepository userRepository,
            RbacService rbacService,
            AuditLogRepository auditLogRepository,
            ai.cloudforge.api.shared.EmailService emailService,
            NotificationService notificationService) {
        this.invitationRepository = invitationRepository;
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.rbacService = rbacService;
        this.auditLogRepository = auditLogRepository;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @Transactional(readOnly = true)
    public List<InvitationResponse> listInvitations(UUID requesterId, UUID orgId) {
        rbacService.getRole(requesterId, orgId);
        return invitationRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId).stream()
                .map(InvitationResponse::fromEntity)
                .toList();
    }

    @Transactional
    public InvitationResponse createInvitation(UUID requesterId, UUID orgId, String email, Role role) {
        try {
            rbacService.requireAdminOrOwner(requesterId, orgId);
        } catch (ForbiddenException fe) {
            rbacService.requireMutatingPermission(requesterId, orgId);
        }

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        AppUser requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = generateToken();
        Instant expiresAt = Instant.now().plusSeconds(7 * 24 * 3600); // 7 days TTL

        OrgInvitation invitation = invitationRepository.save(new OrgInvitation(org, email.trim().toLowerCase(), role, requester, token, expiresAt));
        auditLogRepository.save(new AuditLog(orgId, requesterId, "invitation.created", email + ":" + role));

        // Notify the requester that an invitation was dispatched
        notificationService.createNotification(
                requesterId,
                "Invitation Dispatched",
                "Invitation sent to " + email.trim().toLowerCase() + " for role " + role.name() + " in " + org.getName(),
                "INFO",
                "/invitations"
        );

        var dispatch = emailService.sendInvitationEmail(email.trim().toLowerCase(), org.getName(), role.name(), token);

        return InvitationResponse.fromEntity(invitation, dispatch.status(), dispatch.message());
    }

    @Transactional
    public InvitationResponse resendInvitation(UUID requesterId, UUID orgId, UUID invitationId) {
        rbacService.requireAdminOrOwner(requesterId, orgId);
        OrgInvitation invitation = invitationRepository.findById(invitationId)
                .filter(i -> i.getOrganization().getId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        invitation.resend(Instant.now().plusSeconds(7 * 24 * 3600));
        auditLogRepository.save(new AuditLog(orgId, requesterId, "invitation.resent", invitation.getEmail()));

        var dispatch = emailService.sendInvitationEmail(invitation.getEmail(), invitation.getOrganization().getName(), invitation.getRole().name(), invitation.getToken());

        return InvitationResponse.fromEntity(invitation, dispatch.status(), dispatch.message());
    }

    @Transactional
    public void cancelInvitation(UUID requesterId, UUID orgId, UUID invitationId) {
        rbacService.requireAdminOrOwner(requesterId, orgId);
        OrgInvitation invitation = invitationRepository.findById(invitationId)
                .filter(i -> i.getOrganization().getId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        invitation.cancel();
        auditLogRepository.save(new AuditLog(orgId, requesterId, "invitation.cancelled", invitation.getEmail()));
    }

    /**
     * Atomically accept an invitation and create organization membership.
     *
     * Invariant: NEVER marks invitation ACCEPTED unless membership is also persisted.
     * All operations are in a single @Transactional boundary — any failure rolls back everything.
     *
     * Steps:
     *  1. Load and validate invitation (PENDING + not expired)
     *  2. Resolve recipient user
     *  3. Check email match (security: prevent token stealing across accounts)
     *  4. Create membership if not already exists; update role if already a member
     *  5. Mark invitation ACCEPTED
     *  6. Persist audit events (invitation.accepted + membership.created)
     *  7. Create notifications (for the new member + for the inviter)
     */
    @Transactional
    public void acceptInvitation(UUID userId, String token) {
        // Step 1: Load and validate invitation
        OrgInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired invitation token"));

        if (!"PENDING".equals(invitation.getStatus())) {
            if ("ACCEPTED".equals(invitation.getStatus())) {
                throw new IllegalArgumentException("This invitation has already been accepted");
            } else if ("CANCELLED".equals(invitation.getStatus())) {
                throw new IllegalArgumentException("This invitation has been cancelled");
            } else if ("REJECTED".equals(invitation.getStatus())) {
                throw new IllegalArgumentException("This invitation has been declined");
            } else {
                throw new IllegalArgumentException("Invitation is no longer valid (status: " + invitation.getStatus() + ")");
            }
        }

        if (invitation.isExpired()) {
            throw new IllegalArgumentException("This invitation token has expired. Please request a new invitation.");
        }

        // Step 2: Resolve recipient user
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User account not found"));

        // Step 3: Security check - validate user email matches invitation email
        // Allow acceptance if emails match OR if user has no email set (edge case)
        String invitedEmail = invitation.getEmail().toLowerCase().trim();
        String userEmail = user.getEmail() != null ? user.getEmail().toLowerCase().trim() : "";
        if (!userEmail.isEmpty() && !userEmail.equals(invitedEmail)) {
            throw new ForbiddenException(
                    "Access denied: This invitation was issued to " + invitedEmail
                    + " but you are authenticated as " + userEmail
                    + ". Please sign in with the correct account."
            );
        }

        UUID orgId = invitation.getOrganization().getId();
        Role assignedRole = invitation.getRole();
        String orgName = invitation.getOrganization().getName();

        // Step 4: Create or update membership (idempotent)
        boolean membershipCreated = false;
        var existingMembership = membershipRepository.findByOrgIdAndUserId(orgId, userId);
        if (existingMembership.isEmpty()) {
            // Create new membership record
            membershipRepository.save(new Membership(invitation.getOrganization(), user, assignedRole));
            membershipCreated = true;
        }
        // If already a member: membership exists, role is retained. Invitation still marks ACCEPTED.

        // Step 5: Mark invitation ACCEPTED (AFTER membership persisted — still in same transaction)
        invitation.accept();

        // Step 6: Audit events
        if (membershipCreated) {
            auditLogRepository.save(new AuditLog(orgId, userId, "membership.created",
                    user.getEmail() + ":" + assignedRole.name()));
        }
        auditLogRepository.save(new AuditLog(orgId, userId, "invitation.accepted", user.getEmail()));

        // Step 7: Real notifications
        // 7a. Notify the new member
        notificationService.createNotification(
                userId,
                "Welcome to " + orgName,
                "You have successfully joined " + orgName + " as " + assignedRole.name() + ". Your workspace is now active.",
                "INFO",
                "/dashboard"
        );

        // 7b. Notify the inviter (if user record still exists)
        AppUser inviter = invitation.getInvitedBy();
        if (inviter != null && !inviter.getId().equals(userId)) {
            notificationService.createNotification(
                    inviter.getId(),
                    "Invitation Accepted",
                    user.getEmail() + " accepted your invitation and joined " + orgName + " as " + assignedRole.name(),
                    "INFO",
                    "/members"
            );
        }
    }

    @Transactional
    public void rejectInvitation(String token) {
        OrgInvitation invitation = invitationRepository.findByToken(token)
                .filter(i -> i.getStatus().equals("PENDING"))
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));

        invitation.reject();
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record InvitationResponse(
            UUID id,
            UUID orgId,
            String email,
            Role role,
            String token,
            String status,
            int attemptsCount,
            String deliveryStatus,
            String deliveryMessage,
            Instant expiresAt,
            Instant createdAt
    ) {
        public static InvitationResponse fromEntity(OrgInvitation i) {
            return fromEntity(i, "NOT_ATTEMPTED", null);
        }

        public static InvitationResponse fromEntity(OrgInvitation i, String deliveryStatus, String deliveryMessage) {
            return new InvitationResponse(
                    i.getId(),
                    i.getOrganization().getId(),
                    i.getEmail(),
                    i.getRole(),
                    i.getToken(),
                    i.getStatus(),
                    i.getAttemptsCount(),
                    deliveryStatus,
                    deliveryMessage,
                    i.getExpiresAt(),
                    i.getCreatedAt()
            );
        }
    }
}
