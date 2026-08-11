package ai.cloudforge.api.auth;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.PermissionEvaluatorService.RolePermissionMapping;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    private final PermissionEvaluatorService permissionEvaluatorService;

    public PermissionController(PermissionEvaluatorService permissionEvaluatorService) {
        this.permissionEvaluatorService = permissionEvaluatorService;
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<PermissionResponse>> getCatalog() {
        List<PermissionResponse> catalog = permissionEvaluatorService.getAllPermissions().stream()
                .map(p -> new PermissionResponse(p.getId(), p.getCode(), p.getModule(), p.getDescription()))
                .toList();
        return ResponseEntity.ok(catalog);
    }

    @GetMapping("/matrix")
    public ResponseEntity<List<RolePermissionMapping>> getMatrix() {
        return ResponseEntity.ok(permissionEvaluatorService.getRolePermissionMatrix());
    }

    @GetMapping("/orgs/{orgId}/my-permissions")
    public ResponseEntity<Set<String>> getMyPermissions(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        return ResponseEntity.ok(permissionEvaluatorService.getPermissionsForUserInOrg(principal.userId(), orgId));
    }

    public record PermissionResponse(
            UUID id,
            String code,
            String module,
            String description
    ) {}
}
