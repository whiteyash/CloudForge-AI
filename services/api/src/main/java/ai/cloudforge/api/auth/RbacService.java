package ai.cloudforge.api.auth;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class RbacService {

    private final MembershipRepository membershipRepository;
    private final PermissionEvaluatorService permissionEvaluatorService;

    public RbacService(MembershipRepository membershipRepository, PermissionEvaluatorService permissionEvaluatorService) {
        this.membershipRepository = membershipRepository;
        this.permissionEvaluatorService = permissionEvaluatorService;
    }

    public Role getRole(UUID userId, UUID orgId) {
        return membershipRepository.findByOrgIdAndUserId(orgId, userId)
                .map(Membership::getRole)
                .orElseThrow(() -> new ForbiddenException("Access denied: Not a member of this organization"));
    }

    public void requireMutatingPermission(UUID userId, UUID orgId) {
        Role role = getRole(userId, orgId);
        if (role == Role.VIEWER) {
            throw new ForbiddenException("Access denied: Viewer role is read-only");
        }
    }

    public void requireAdminOrOwner(UUID userId, UUID orgId) {
        Role role = getRole(userId, orgId);
        if (role != Role.OWNER && role != Role.ADMIN) {
            throw new ForbiddenException("Access denied: Action requires Admin or Owner role");
        }
    }

    public void requireOwner(UUID userId, UUID orgId) {
        Role role = getRole(userId, orgId);
        if (role != Role.OWNER) {
            throw new ForbiddenException("Access denied: Action requires Organization Owner role");
        }
    }

    public void requirePermission(UUID userId, UUID orgId, String permissionCode) {
        if (!permissionEvaluatorService.hasPermission(userId, orgId, permissionCode)) {
            throw new ForbiddenException("Access denied: Missing required permission " + permissionCode);
        }
    }
}
