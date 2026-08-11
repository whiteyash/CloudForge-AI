package ai.cloudforge.api.ai.rca;

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

class RootCauseControllerTest {

    private RootCauseAnalysisService service;
    private RootCauseController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(RootCauseAnalysisService.class);
        controller = new RootCauseController(service);
    }

    @Test
    void testListRootCausesEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getReportsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<RootCauseAnalysisService.RootCauseReportResponse>> response = controller.listRootCauses(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetGraphsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getGraphForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<DependencyGraphService.DependencyLinkResponse>> response = controller.getGraphs(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
