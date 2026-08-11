package ai.cloudforge.api.project;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;
import ai.cloudforge.api.project.ProjectDtos.CreateProjectRequest;
import ai.cloudforge.api.project.ProjectDtos.ProjectResponse;

@RestController
@RequestMapping("/orgs/{orgId}/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> listProjects(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId) {
        return ResponseEntity.ok(projectService.getProjectsForOrg(principal.userId(), orgId));
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.getProject(principal.userId(), orgId, projectId));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID orgId,
            @Valid @RequestBody CreateProjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(projectService.createProject(principal.userId(), orgId, request));
    }
}
