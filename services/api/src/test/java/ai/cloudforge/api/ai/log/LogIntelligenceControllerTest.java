package ai.cloudforge.api.ai.log;

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

class LogIntelligenceControllerTest {

    private LogIntelligenceService service;
    private LogSummaryService summaryService;
    private LogIntelligenceController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(LogIntelligenceService.class);
        summaryService = Mockito.mock(LogSummaryService.class);
        controller = new LogIntelligenceController(service, summaryService);
    }

    @Test
    void testListLogsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getLogsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<LogIntelligenceService.LogEntryResponse>> response = controller.listLogs(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testListClustersEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getClustersForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<LogIntelligenceService.ClusterResponse>> response = controller.listClusters(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
