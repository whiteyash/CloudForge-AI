package ai.cloudforge.api.auth;

import java.util.Set;

public final class PermissionContextHolder {

    private static final ThreadLocal<Set<String>> PERMISSION_CONTEXT = new ThreadLocal<>();

    private PermissionContextHolder() {
    }

    public static void setPermissions(Set<String> permissions) {
        PERMISSION_CONTEXT.set(permissions);
    }

    public static Set<String> getPermissions() {
        return PERMISSION_CONTEXT.get();
    }

    public static boolean hasPermission(String permissionCode) {
        Set<String> perms = PERMISSION_CONTEXT.get();
        return perms != null && perms.contains(permissionCode);
    }

    public static void clear() {
        PERMISSION_CONTEXT.remove();
    }
}
