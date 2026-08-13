package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orgs")
public class OrganizationController {

    private final RbacService rbacService;
    private final MembershipRepository membershipRepository;
    private final AppUserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRoleHistoryRepository roleHistoryRepository;
    private final AuditLogRepository auditLogRepository;
    private final ai.cloudforge.api.project.ProjectRepository projectRepository;

    public OrganizationController(
            RbacService rbacService,
            MembershipRepository membershipRepository,
            AppUserRepository userRepository,
            OrganizationRepository organizationRepository,
            MembershipRoleHistoryRepository roleHistoryRepository,
            AuditLogRepository auditLogRepository,
            ai.cloudforge.api.project.ProjectRepository projectRepository) {
        this.rbacService = rbacService;
        this.membershipRepository = membershipRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.roleHistoryRepository = roleHistoryRepository;
        this.auditLogRepository = auditLogRepository;
        this.projectRepository = projectRepository;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<OrgCardResponse>> listUserOrgs(@AuthenticationPrincipal AuthPrincipal principal) {
        List<Membership> memberships = membershipRepository.findByUserId(principal.userId());
        List<OrgCardResponse> result = memberships.stream().map(m -> {
            Organization org = m.getOrganization();
            long membersCount = membershipRepository.findByOrganizationId(org.getId()).size();
            long projectsCount = projectRepository.findByOrganizationId(org.getId()).size();
            return new OrgCardResponse(
                    org.getId(),
                    org.getName(),
                    org.getSlug(),
                    org.getDescription(),
                    m.getRole().name(),
                    membersCount,
                    projectsCount,
                    "ENTERPRISE",
                    org.getStatus(),
                    org.getCreatedAt()
            );
        }).toList();
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<OrgDetailResponse> createOrg(
            @AuthenticationPrincipal AuthPrincipal principal,
            @Valid @RequestBody CreateOrgRequest request) {
        String baseSlug = request.slug().trim().toLowerCase().replaceAll("[^a-z0-9-]", "-");
        String slug = baseSlug;
        while (organizationRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + UUID.randomUUID().toString().substring(0, 8);
        }
        Organization org = organizationRepository.save(new Organization(request.name().trim(), slug));
        AppUser user = userRepository.findById(principal.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        membershipRepository.save(new Membership(org, user, Role.OWNER));
        auditLogRepository.save(new AuditLog(org.getId(), principal.userId(), "organization.created", org.getSlug()));
        return ResponseEntity.ok(OrgDetailResponse.fromEntity(org));
    }

    @GetMapping("/{orgId}")
    public ResponseEntity<OrgDetailResponse> getOrg(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        rbacService.getRole(principal.userId(), orgId);
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        return ResponseEntity.ok(OrgDetailResponse.fromEntity(org));
    }

    @PatchMapping("/{orgId}")
    public ResponseEntity<OrgDetailResponse> updateOrg(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @RequestBody UpdateOrgRequest request) {
        rbacService.requireAdminOrOwner(principal.userId(), orgId);
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        org.update(request.name(), request.description(), request.websiteUrl(), request.timezone(), request.primaryColor());
        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "organization.updated", org.getSlug()));
        return ResponseEntity.ok(OrgDetailResponse.fromEntity(org));
    }

    @PostMapping("/{orgId}/archive")
    public ResponseEntity<Void> archiveOrg(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        rbacService.requireAdminOrOwner(principal.userId(), orgId);
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        org.archive();
        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "organization.archived", org.getSlug()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orgId}/restore")
    public ResponseEntity<Void> restoreOrg(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        rbacService.requireAdminOrOwner(principal.userId(), orgId);
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        org.restore();
        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "organization.restored", org.getSlug()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orgId}")
    public ResponseEntity<Void> deleteOrg(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        rbacService.requireAdminOrOwner(principal.userId(), orgId);
        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));
        org.softDelete();
        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "organization.deleted", org.getSlug()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orgId}/members")
    public ResponseEntity<List<MemberResponse>> getMembers(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        rbacService.getRole(principal.userId(), orgId);
        List<MemberResponse> members = membershipRepository.findByOrganizationIdWithUser(orgId).stream()
                .map(m -> new MemberResponse(
                        m.getId(),
                        m.getUser().getId(),
                        m.getUser().getEmail(),
                        m.getUser().getFullName(),
                        m.getRole().name(),
                        m.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(members);
    }

    @PostMapping("/{orgId}/members")
    public ResponseEntity<MemberResponse> addMember(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @Valid @RequestBody InviteMemberRequest request) {
        rbacService.requireAdminOrOwner(principal.userId(), orgId);

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        AppUser user = userRepository.findByEmailIgnoreCase(request.email().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.email()));

        if (membershipRepository.findByOrgIdAndUserId(orgId, user.getId()).isPresent()) {
            throw new IllegalArgumentException("User is already a member of this organization");
        }

        Membership membership = membershipRepository.save(new Membership(org, user, request.role()));
        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "member.added", user.getEmail() + ":" + request.role()));

        return ResponseEntity.ok(new MemberResponse(
                membership.getId(),
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                membership.getRole().name(),
                membership.getCreatedAt()
        ));
    }

    @PatchMapping("/{orgId}/members/{userId}/role")
    public ResponseEntity<MemberResponse> updateMemberRole(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID userId,
            @RequestBody UpdateRoleRequest request) {
        rbacService.requireOwner(principal.userId(), orgId);

        Membership target = membershipRepository.findByOrgIdAndUserId(orgId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (target.getRole() == Role.OWNER && request.newRole() != Role.OWNER) {
            long ownerCount = membershipRepository.findAll().stream()
                    .filter(m -> m.getOrganization().getId().equals(orgId) && m.getRole() == Role.OWNER)
                    .count();
            if (ownerCount <= 1) {
                throw new IllegalArgumentException("Cannot downgrade the last remaining owner of the organization");
            }
        }

        Role oldRole = target.getRole();
        target.updateRole(request.newRole());
        membershipRepository.save(target);

        roleHistoryRepository.save(new MembershipRoleHistory(orgId, userId, oldRole.name(), request.newRole().name(), principal.userId()));
        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "member.role_updated", target.getUser().getEmail() + ":" + request.newRole()));

        return ResponseEntity.ok(new MemberResponse(
                target.getId(),
                target.getUser().getId(),
                target.getUser().getEmail(),
                target.getUser().getFullName(),
                target.getRole().name(),
                target.getCreatedAt()
        ));
    }

    @PostMapping("/{orgId}/transfer-ownership")
    public ResponseEntity<Void> transferOwnership(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @RequestBody TransferOwnershipRequest request) {
        rbacService.requireOwner(principal.userId(), orgId);

        Membership target = membershipRepository.findByOrgIdAndUserId(orgId, request.targetUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Target user is not a member of the organization"));

        target.updateRole(Role.OWNER);
        membershipRepository.save(target);

        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "organization.ownership_transferred", target.getUser().getEmail()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{orgId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID userId) {
        rbacService.requireAdminOrOwner(principal.userId(), orgId);

        Membership target = membershipRepository.findByOrgIdAndUserId(orgId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        if (target.getRole() == Role.OWNER) {
            long ownerCount = membershipRepository.findAll().stream()
                    .filter(m -> m.getOrganization().getId().equals(orgId) && m.getRole() == Role.OWNER)
                    .count();
            if (ownerCount <= 1) {
                throw new IllegalArgumentException("Cannot remove the last remaining owner of the organization");
            }
        }

        membershipRepository.delete(target);
        auditLogRepository.save(new AuditLog(orgId, principal.userId(), "member.removed", target.getUser().getEmail()));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{orgId}/audit-logs")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogs(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        rbacService.getRole(principal.userId(), orgId);
        List<AuditLogResponse> logs = auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId).stream()
                .map(log -> new AuditLogResponse(
                        log.getId(),
                        log.getOrganizationId(),
                        log.getUserId(),
                        log.getAction(),
                        log.getTarget(),
                        log.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok(logs);
    }

    public record OrgDetailResponse(
            UUID id,
            String name,
            String slug,
            String description,
            String logoUrl,
            String websiteUrl,
            String timezone,
            String status,
            String primaryColor,
            Instant createdAt
    ) {
        public static OrgDetailResponse fromEntity(Organization org) {
            return new OrgDetailResponse(
                    org.getId(),
                    org.getName(),
                    org.getSlug(),
                    org.getDescription(),
                    org.getLogoUrl(),
                    org.getWebsiteUrl(),
                    org.getTimezone(),
                    org.getStatus(),
                    org.getPrimaryColor(),
                    org.getCreatedAt()
            );
        }
    }

    public record CreateOrgRequest(
            @NotNull String name,
            @NotNull String slug
    ) {}

    public record UpdateOrgRequest(
            String name,
            String description,
            String websiteUrl,
            String timezone,
            String primaryColor
    ) {}

    public record InviteMemberRequest(
            @Email @NotNull String email,
            @NotNull Role role
    ) {}

    public record UpdateRoleRequest(
            Role newRole
    ) {}

    public record TransferOwnershipRequest(
            UUID targetUserId
    ) {}

    public record MemberResponse(
            UUID membershipId,
            UUID userId,
            String email,
            String fullName,
            String role,
            Instant createdAt
    ) {}

    public record AuditLogResponse(
            UUID id,
            UUID orgId,
            UUID userId,
            String action,
            String target,
            Instant createdAt
    ) {}

    public record OrgCardResponse(
            UUID id,
            String name,
            String slug,
            String description,
            String role,
            long membersCount,
            long projectsCount,
            String plan,
            String status,
            Instant createdAt
    ) {}
}
