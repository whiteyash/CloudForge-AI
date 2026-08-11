package ai.cloudforge.api.job;

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

class JobExecutionServiceTest {

    private JobExecutionRepository executionRepository;
    private JobLogRepository logRepository;
    private EventPublisher eventPublisher;
    private JobExecutionService service;

    @BeforeEach
    void setUp() {
        executionRepository = Mockito.mock(JobExecutionRepository.class);
        logRepository = Mockito.mock(JobLogRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new JobExecutionService(executionRepository, logRepository, eventPublisher);
    }

    @Test
    void testStartAndCompleteJob() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID runId = UUID.randomUUID();

        when(executionRepository.save(any(JobExecution.class))).thenAnswer(inv -> inv.getArgument(0));

        JobExecutionService.JobResponse response = service.startJob(orgId, userId, runId, "unit-test-job", null);

        assertNotNull(response);
        assertEquals("unit-test-job", response.jobName());
        assertEquals("RUNNING", response.status());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));

        // Complete job with exit code 0
        JobExecution job = new JobExecution(runId, "unit-test-job", null);
        when(executionRepository.findById(job.getId())).thenReturn(Optional.of(job));

        JobExecutionService.JobResponse completedResponse = service.completeJob(orgId, userId, job.getId(), 0);
        assertEquals("SUCCESS", completedResponse.status());
        assertEquals(0, completedResponse.exitCode());
    }

    @Test
    void testAppendAndQueryLogs() {
        UUID jobId = UUID.randomUUID();
        when(logRepository.findByJobExecutionIdOrderBySequenceNumberAsc(jobId)).thenReturn(List.of());
        when(logRepository.save(any(JobLog.class))).thenAnswer(inv -> inv.getArgument(0));

        JobExecutionService.LogResponse logResponse = service.appendLog(jobId, "Building container image...", "STDOUT");

        assertNotNull(logResponse);
        assertEquals(1, logResponse.sequenceNumber());
        assertEquals("Building container image...", logResponse.logLine());
    }
}
