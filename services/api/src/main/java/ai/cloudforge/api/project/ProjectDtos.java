package ai.cloudforge.api.project;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProjectDtos {

    public record CreateProjectRequest(
            @NotBlank(message = "Project name is required")
            @Size(max = 120, message = "Project name must be 120 characters or fewer")
            String name,
            String repoUrl,
            String k8sNamespace
    ) {}

    public record ProjectResponse(
            UUID id,
            UUID orgId,
            String name,
            String repoUrl,
            String k8sNamespace,
            Instant createdAt
    ) {
        public static ProjectResponse fromEntity(Project project) {
            return new ProjectResponse(
                    project.getId(),
                    project.getOrganization().getId(),
                    project.getName(),
                    project.getRepoUrl(),
                    project.getK8sNamespace(),
                    project.getCreatedAt()
            );
        }
    }
}
