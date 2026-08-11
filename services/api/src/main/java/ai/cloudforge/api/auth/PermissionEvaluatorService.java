package ai.cloudforge.api.auth;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionEvaluatorService {

    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final MembershipRepository membershipRepository;
    private final AuditLogRepository auditLogRepository;

    public PermissionEvaluatorService(
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            MembershipRepository membershipRepository,
            AuditLogRepository auditLogRepository) {
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.membershipRepository = membershipRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(UUID userId, UUID orgId, String permissionCode) {
        Role role = membershipRepository.findByOrgIdAndUserId(orgId, userId)
                .map(Membership::getRole)
                .orElse(null);

        if (role == null) {
            logPermissionDenied(userId, orgId, permissionCode, "User is not a member of the organization");
            return false;
        }

        Set<String> grantedPermissions = rolePermissionRepository.findByRole(role).stream()
                .map(RolePermission::getPermissionCode)
                .collect(Collectors.toSet());

        boolean hasGranted = grantedPermissions.contains(permissionCode);
        if (!hasGranted) {
            logPermissionDenied(userId, orgId, permissionCode, "Role " + role + " lacks permission " + permissionCode);
        }
        return hasGranted;
    }

    @Transactional(readOnly = true)
    public Set<String> getPermissionsForUserInOrg(UUID userId, UUID orgId) {
        Role role = membershipRepository.findByOrgIdAndUserId(orgId, userId)
                .map(Membership::getRole)
                .orElseThrow(() -> new ForbiddenException("Access denied: Not a member of organization"));

        return rolePermissionRepository.findByRole(role).stream()
                .map(RolePermission::getPermissionCode)
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAllByOrderByModuleAscCodeAsc();
    }

    @Transactional(readOnly = true)
    public List<RolePermissionMapping> getRolePermissionMatrix() {
        return rolePermissionRepository.findAll().stream()
                .map(rp -> new RolePermissionMapping(rp.getRole().name(), rp.getPermissionCode()))
                .toList();
    }

    private void logPermissionDenied(UUID userId, UUID orgId, String permissionCode, String reason) {
        auditLogRepository.save(new AuditLog(orgId, userId, "permission.denied", permissionCode + ":" + reason));
    }

    public record RolePermissionMapping(
            String role,
            String permissionCode
    ) {}
}
