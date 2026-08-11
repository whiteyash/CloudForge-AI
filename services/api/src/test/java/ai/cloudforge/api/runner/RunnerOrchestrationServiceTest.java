package ai.cloudforge.api.runner;

import java.util.List;
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

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class RunnerOrchestrationServiceTest {

    private RunnerRepository runnerRepository;
    private EventPublisher eventPublisher;
    private RunnerOrchestrationService service;

    @BeforeEach
    void setUp() {
        runnerRepository = Mockito.mock(RunnerRepository.class);
        RunnerAssignmentRepository assignmentRepository = Mockito.mock(RunnerAssignmentRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RunnerOrchestrationService(runnerRepository, assignmentRepository, eventPublisher);
    }

    @Test
    void testRegisterRunner() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(runnerRepository.save(any(Runner.class))).thenAnswer(inv -> inv.getArgument(0));

        RunnerOrchestrationService.RunnerResponse response = service.registerRunner(
                orgId, userId, projectId, "k8s-runner-1", "KUBERNETES", "default", "secret_token_123",
                "ubuntu-latest,docker", "linux", 4
        );

        assertNotNull(response);
        assertEquals("k8s-runner-1", response.name());
        assertEquals("KUBERNETES", response.runnerType());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testHeartbeatAndDrain() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID runnerId = UUID.randomUUID();
        Runner runner = new Runner(UUID.randomUUID(), "docker-agent", "DOCKER", "default", "hash123", "ubuntu-latest", "linux", 2);

        when(runnerRepository.findById(runnerId)).thenReturn(Optional.of(runner));
        when(runnerRepository.save(any(Runner.class))).thenAnswer(inv -> inv.getArgument(0));

        RunnerOrchestrationService.RunnerResponse response = service.heartbeat(runnerId);
        assertNotNull(response);
        assertEquals("ONLINE", response.status());

        RunnerOrchestrationService.RunnerResponse drainResponse = service.setRunnerStatus(orgId, userId, runnerId, "DRAINING");
        assertEquals("DRAINING", drainResponse.status());
    }
}
