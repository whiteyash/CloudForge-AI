package ai.cloudforge.api.auth;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PermissionAspect {

    private final RbacService rbacService;

    public PermissionAspect(RbacService rbacService) {
        this.rbacService = rbacService;
    }

    @Before("@annotation(requirePermission)")
    public void checkPermission(JoinPoint joinPoint, RequirePermission requirePermission) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthPrincipal principal)) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = TenantContext.getTenantId();
        if (orgId == null) {
            orgId = extractOrgIdFromArgs(joinPoint.getArgs());
        }

        if (orgId == null) {
            throw new IllegalArgumentException("Organization context missing for permission evaluation");
        }

        rbacService.requirePermission(principal.userId(), orgId, requirePermission.value());
    }

    private UUID extractOrgIdFromArgs(Object[] args) {
        if (args == null) return null;
        for (Object arg : args) {
            if (arg instanceof UUID uuid) {
                return uuid;
            }
        }
        return null;
    }
}
