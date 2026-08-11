package ai.cloudforge.api.auth;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrgInvitationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final OrgInvitationRepository invitationRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final AppUserRepository userRepository;
    private final RbacService rbacService;
    private final AuditLogRepository auditLogRepository;

    public OrgInvitationService(
            OrgInvitationRepository invitationRepository,
            OrganizationRepository organizationRepository,
            MembershipRepository membershipRepository,
            AppUserRepository userRepository,
            RbacService rbacService,
            AuditLogRepository auditLogRepository) {
        this.invitationRepository = invitationRepository;
        this.organizationRepository = organizationRepository;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.rbacService = rbacService;
        this.auditLogRepository = auditLogRepository;
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
        rbacService.requireAdminOrOwner(requesterId, orgId);

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        AppUser requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = generateToken();
        Instant expiresAt = Instant.now().plusSeconds(7 * 24 * 3600); // 7 days TTL

        OrgInvitation invitation = invitationRepository.save(new OrgInvitation(org, email.trim().toLowerCase(), role, requester, token, expiresAt));
        auditLogRepository.save(new AuditLog(orgId, requesterId, "invitation.created", email + ":" + role));

        return InvitationResponse.fromEntity(invitation);
    }

    @Transactional
    public InvitationResponse resendInvitation(UUID requesterId, UUID orgId, UUID invitationId) {
        rbacService.requireAdminOrOwner(requesterId, orgId);
        OrgInvitation invitation = invitationRepository.findById(invitationId)
                .filter(i -> i.getOrganization().getId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found"));

        invitation.resend(Instant.now().plusSeconds(7 * 24 * 3600));
        auditLogRepository.save(new AuditLog(orgId, requesterId, "invitation.resent", invitation.getEmail()));
        return InvitationResponse.fromEntity(invitation);
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

    @Transactional
    public void acceptInvitation(UUID userId, String token) {
        OrgInvitation invitation = invitationRepository.findByToken(token)
                .filter(i -> i.getStatus().equals("PENDING") && !i.isExpired())
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired invitation token"));

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (membershipRepository.findByOrgIdAndUserId(invitation.getOrganization().getId(), userId).isEmpty()) {
            membershipRepository.save(new Membership(invitation.getOrganization(), user, invitation.getRole()));
            auditLogRepository.save(new AuditLog(invitation.getOrganization().getId(), userId, "invitation.accepted", user.getEmail()));
        }

        invitation.accept();
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
            Instant expiresAt,
            Instant createdAt
    ) {
        public static InvitationResponse fromEntity(OrgInvitation i) {
            return new InvitationResponse(
                    i.getId(),
                    i.getOrganization().getId(),
                    i.getEmail(),
                    i.getRole(),
                    i.getToken(),
                    i.getStatus(),
                    i.getAttemptsCount(),
                    i.getExpiresAt(),
                    i.getCreatedAt()
            );
        }
    }
}
