package ai.cloudforge.api.project;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.AuditLog;
import ai.cloudforge.api.auth.AuditLogRepository;
import ai.cloudforge.api.auth.Organization;
import ai.cloudforge.api.auth.OrganizationRepository;

import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.project.ProjectDtos.CreateProjectRequest;
import ai.cloudforge.api.project.ProjectDtos.ProjectResponse;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final RbacService rbacService;
    private final AuditLogRepository auditLogRepository;

    public ProjectService(
            ProjectRepository projectRepository,
            OrganizationRepository organizationRepository,
            RbacService rbacService,
            AuditLogRepository auditLogRepository) {
        this.projectRepository = projectRepository;
        this.organizationRepository = organizationRepository;
        this.rbacService = rbacService;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjectsForOrg(UUID userId, UUID orgId) {
        rbacService.getRole(userId, orgId); // Verifies membership
        return projectRepository.findByOrganizationId(orgId).stream()
                .map(ProjectResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(UUID userId, UUID orgId, UUID projectId) {
        rbacService.getRole(userId, orgId);
        Project project = projectRepository.findByIdAndOrganizationId(projectId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found in organization"));
        return ProjectResponse.fromEntity(project);
    }

    @Transactional
    public ProjectResponse createProject(UUID userId, UUID orgId, CreateProjectRequest request) {
        rbacService.requireMutatingPermission(userId, orgId); // Enforces non-VIEWER

        Organization org = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found"));

        if (projectRepository.existsByOrganizationIdAndNameIgnoreCase(orgId, request.name().trim())) {
            throw new IllegalArgumentException("A project with this name already exists in the organization");
        }

        Project project = projectRepository.save(new Project(
                org,
                request.name().trim(),
                request.repoUrl() != null ? request.repoUrl().trim() : null,
                request.k8sNamespace() != null ? request.k8sNamespace().trim() : "default"
        ));

        auditLogRepository.save(new AuditLog(orgId, userId, "project.created", project.getName()));

        return ProjectResponse.fromEntity(project);
    }

    @Transactional
    public void deleteProject(UUID userId, UUID orgId, UUID projectId) {
        rbacService.requireMutatingPermission(userId, orgId);
        Project project = projectRepository.findByIdAndOrganizationId(projectId, orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found in organization"));
        projectRepository.delete(project);
        auditLogRepository.save(new AuditLog(orgId, userId, "project.deleted", project.getName()));
    }
}
