package ai.cloudforge.api.deployment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.deployment.adapter.DeploymentAdapter;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class DeploymentEngineService {

    private final DeploymentRepository deploymentRepository;
    private final DeploymentRollbackRepository rollbackRepository;
    private final DeploymentAdapter deploymentAdapter;
    private final EventPublisher eventPublisher;

    public DeploymentEngineService(
            DeploymentRepository deploymentRepository,
            DeploymentRollbackRepository rollbackRepository,
            DeploymentAdapter deploymentAdapter,
            EventPublisher eventPublisher) {
        this.deploymentRepository = deploymentRepository;
        this.rollbackRepository = rollbackRepository;
        this.deploymentAdapter = deploymentAdapter;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<DeploymentResponse> getDeploymentsForProject(UUID projectId) {
        return deploymentRepository.findByProjectId(projectId).stream()
                .map(DeploymentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeploymentResponse getDeploymentById(UUID deploymentId) {
        return deploymentRepository.findById(deploymentId)
                .map(DeploymentResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Deployment record not found"));
    }

    @Transactional
    public DeploymentResponse createDeployment(
            UUID orgId,
            UUID userId,
            UUID projectId,
            UUID pipelineRunId,
            UUID artifactId,
            String targetName,
            String strategy,
            String idempotencyKey,
            String userEmail) {

        // Idempotency protection check
        if (idempotencyKey != null) {
            Optional<Deployment> existing = deploymentRepository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) {
                return DeploymentResponse.fromEntity(existing.get());
            }
        }

        String finalKey = idempotencyKey != null ? idempotencyKey : UUID.randomUUID().toString();
        Deployment deployment = new Deployment(projectId, pipelineRunId, artifactId, targetName, strategy, finalKey, userEmail != null ? userEmail : "system");

        if (!"PENDING_APPROVAL".equals(deployment.getStatus())) {
            boolean success = deploymentAdapter.executeDeployment(targetName, strategy, artifactId != null ? artifactId.toString() : "latest");
            deployment.setStatus(success ? "SUCCEEDED" : "FAILED");
        }

        Deployment saved = deploymentRepository.save(deployment);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "DEPLOYMENT_CREATED",
                targetName + " (" + strategy + ")",
                "Deployment requested for " + targetName + " using strategy " + strategy
        ));

        return DeploymentResponse.fromEntity(saved);
    }

    @Transactional
    public DeploymentResponse approveDeployment(UUID orgId, UUID userId, UUID deploymentId, String approverEmail) {
        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Deployment record not found"));

        deployment.approve(approverEmail);
        boolean success = deploymentAdapter.executeDeployment(deployment.getTargetName(), deployment.getStrategy(), "latest");
        deployment.setStatus(success ? "SUCCEEDED" : "FAILED");
        Deployment saved = deploymentRepository.save(deployment);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "DEPLOYMENT_APPROVED",
                saved.getTargetName(),
                "Deployment to " + saved.getTargetName() + " approved by " + approverEmail
        ));

        return DeploymentResponse.fromEntity(saved);
    }

    @Transactional
    public DeploymentResponse cancelDeployment(UUID orgId, UUID userId, UUID deploymentId) {
        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Deployment record not found"));

        deployment.setStatus("CANCELLED");
        Deployment saved = deploymentRepository.save(deployment);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "DEPLOYMENT_CANCELLED",
                saved.getTargetName(),
                "Deployment to " + saved.getTargetName() + " cancelled"
        ));

        return DeploymentResponse.fromEntity(saved);
    }

    @Transactional
    public DeploymentResponse rollbackDeployment(UUID orgId, UUID userId, UUID deploymentId, UUID targetDeploymentId, String reason, String userEmail) {
        Deployment deployment = deploymentRepository.findById(deploymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Deployment record not found"));

        boolean rollbackSuccess = deploymentAdapter.executeRollback(deployment.getTargetName(), targetDeploymentId.toString());
        rollbackRepository.save(new DeploymentRollback(deploymentId, targetDeploymentId, reason, userEmail != null ? userEmail : "system"));

        deployment.setStatus(rollbackSuccess ? "ROLLED_BACK" : "FAILED");
        Deployment saved = deploymentRepository.save(deployment);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "DEPLOYMENT_ROLLBACK_COMPLETED",
                saved.getTargetName(),
                "Deployment to " + saved.getTargetName() + " rolled back successfully: " + reason
        ));

        return DeploymentResponse.fromEntity(saved);
    }

    public record DeploymentResponse(
            UUID id,
            UUID projectId,
            UUID pipelineRunId,
            UUID artifactId,
            String targetName,
            String strategy,
            String status,
            String idempotencyKey,
            String requestedBy,
            String approvedBy,
            String failureReason
    ) {
        public static DeploymentResponse fromEntity(Deployment d) {
            return new DeploymentResponse(
                    d.getId(), d.getProjectId(), d.getPipelineRunId(), d.getArtifactId(),
                    d.getTargetName(), d.getStrategy(), d.getStatus(), d.getIdempotencyKey(),
                    d.getRequestedBy(), d.getApprovedBy(), d.getFailureReason()
            );
        }
    }
}
