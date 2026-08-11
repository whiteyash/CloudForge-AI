package ai.cloudforge.api.job;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class JobExecutionSecurityTest {

    private JobExecutionRepository executionRepository;
    private JobExecutionService service;

    @BeforeEach
    void setUp() {
        executionRepository = Mockito.mock(JobExecutionRepository.class);
        JobLogRepository logRepository = Mockito.mock(JobLogRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new JobExecutionService(executionRepository, logRepository, eventPublisher);
    }

    @Test
    void testUnauthorizedJobAccessThrowsNotFound() {
        UUID jobId = UUID.randomUUID();
        when(executionRepository.findById(jobId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getJobById(jobId);
        });
    }
}
