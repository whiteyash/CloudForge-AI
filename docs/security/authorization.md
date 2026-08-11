# CloudForge AI — Authorization & Permission Engine

## Granular RBAC Engine Architecture
Authorization in CloudForge AI is powered by a custom Spring AOP interceptor (`PermissionAspect`) enforcing declarative method security via `@RequirePermission("code")`.

```java
@RestController
@RequestMapping("/orgs/{orgId}/projects")
public class ProjectController {

    @PostMapping
    @RequirePermission("project.create")
    public ResponseEntity<ProjectResponse> createProject(...) {
        // Enforced declaratively before execution
    }
}
```

### Permission Cache & UI Guards
- **ThreadLocal Cache**: `PermissionContextHolder` caches resolved permissions during HTTP request lifecycles to eliminate redundant DB lookups.
- **Declarative React UI Wrapper**: `<PermissionGuard require="project.create">` conditionally renders UI controls based on active user privileges.
