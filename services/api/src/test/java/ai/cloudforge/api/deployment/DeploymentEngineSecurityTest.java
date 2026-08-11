package ai.cloudforge.api.deployment;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.deployment.adapter.DeploymentAdapter;
import ai.cloudforge.api.notification.EventPublisher;

class DeploymentEngineSecurityTest {

    private DeploymentRepository deploymentRepository;
    private DeploymentEngineService service;

    @BeforeEach
    void setUp() {
        deploymentRepository = Mockito.mock(DeploymentRepository.class);
        DeploymentRollbackRepository rollbackRepository = Mockito.mock(DeploymentRollbackRepository.class);
        DeploymentAdapter deploymentAdapter = Mockito.mock(DeploymentAdapter.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new DeploymentEngineService(deploymentRepository, rollbackRepository, deploymentAdapter, eventPublisher);
    }

    @Test
    void testUnauthorizedDeploymentAccessThrowsNotFound() {
        UUID deploymentId = UUID.randomUUID();
        when(deploymentRepository.findById(deploymentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getDeploymentById(deploymentId);
        });
    }
}
