package ai.cloudforge.api.deployment;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.deployment.adapter.DeploymentAdapter;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class DeploymentEngineServiceTest {

    private DeploymentRepository deploymentRepository;
    private DeploymentRollbackRepository rollbackRepository;
    private DeploymentAdapter deploymentAdapter;
    private EventPublisher eventPublisher;
    private DeploymentEngineService service;

    @BeforeEach
    void setUp() {
        deploymentRepository = Mockito.mock(DeploymentRepository.class);
        rollbackRepository = Mockito.mock(DeploymentRollbackRepository.class);
        deploymentAdapter = Mockito.mock(DeploymentAdapter.class);
        eventPublisher = Mockito.mock(EventPublisher.class);

        when(deploymentAdapter.executeDeployment(any(), any(), any())).thenReturn(true);
        when(deploymentAdapter.executeRollback(any(), any())).thenReturn(true);

        service = new DeploymentEngineService(deploymentRepository, rollbackRepository, deploymentAdapter, eventPublisher);
    }

    @Test
    void testCreateDeploymentAndIdempotency() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        String idempotencyKey = "idempotent-key-123";

        when(deploymentRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.empty());
        when(deploymentRepository.save(any(Deployment.class))).thenAnswer(inv -> inv.getArgument(0));

        DeploymentEngineService.DeploymentResponse response = service.createDeployment(
                orgId, userId, projectId, null, null, "STAGING", "ROLLING", idempotencyKey, "lead@cloudforge.ai"
        );

        assertNotNull(response);
        assertEquals("STAGING", response.targetName());
        assertEquals("SUCCEEDED", response.status());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testApproveAndRollbackDeployment() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID deploymentId = UUID.randomUUID();
        UUID targetDeploymentId = UUID.randomUUID();
        Deployment deployment = new Deployment(UUID.randomUUID(), null, null, "PRODUCTION", "CANARY", "key-prod", "lead@cloudforge.ai");

        when(deploymentRepository.findById(deploymentId)).thenReturn(Optional.of(deployment));
        when(deploymentRepository.save(any(Deployment.class))).thenAnswer(inv -> inv.getArgument(0));

        DeploymentEngineService.DeploymentResponse approvedResponse = service.approveDeployment(orgId, userId, deploymentId, "approver@cloudforge.ai");
        assertEquals("SUCCEEDED", approvedResponse.status());

        DeploymentEngineService.DeploymentResponse rollbackResponse = service.rollbackDeployment(orgId, userId, deploymentId, targetDeploymentId, "Emergency rollback", "lead@cloudforge.ai");
        assertEquals("ROLLED_BACK", rollbackResponse.status());
    }
}
