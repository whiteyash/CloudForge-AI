package ai.cloudforge.api.job;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class JobExecutionControllerTest {

    private JobExecutionService service;
    private JobExecutionController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(JobExecutionService.class);
        controller = new JobExecutionController(service);
    }

    @Test
    void testGetJobsForRunEndpoint() {
        UUID runId = UUID.randomUUID();
        when(service.getJobsForRun(runId)).thenReturn(List.of());

        ResponseEntity<List<JobExecutionService.JobResponse>> response = controller.getJobsForRun(runId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testAppendLogEndpoint() {
        UUID jobId = UUID.randomUUID();
        JobExecutionController.AppendLogRequest request = new JobExecutionController.AppendLogRequest("Step 1 completed", "STDOUT");

        when(service.appendLog(jobId, "Step 1 completed", "STDOUT"))
                .thenReturn(new JobExecutionService.LogResponse(UUID.randomUUID(), jobId, 1, "Step 1 completed", "STDOUT"));

        ResponseEntity<JobExecutionService.LogResponse> response = controller.appendLog(jobId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Step 1 completed", response.getBody().logLine());
    }
}
