package ai.cloudforge.api.runner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class RunnerOrchestrationSecurityTest {

    private RunnerRepository runnerRepository;
    private RunnerOrchestrationService service;

    @BeforeEach
    void setUp() {
        runnerRepository = Mockito.mock(RunnerRepository.class);
        RunnerAssignmentRepository assignmentRepository = Mockito.mock(RunnerAssignmentRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new RunnerOrchestrationService(runnerRepository, assignmentRepository, eventPublisher);
    }

    @Test
    void testUnauthorizedRunnerAccessThrowsNotFound() {
        UUID runnerId = UUID.randomUUID();
        when(runnerRepository.findById(runnerId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getRunnerById(runnerId);
        });
    }
}
