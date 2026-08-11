package ai.cloudforge.api.observability;

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

class ObservabilityControllerTest {

    private ObservabilityService service;
    private ObservabilityController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(ObservabilityService.class);
        controller = new ObservabilityController(service);
    }

    @Test
    void testGetOverviewEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getOverviewForProject(projectId))
                .thenReturn(new ObservabilityService.AnalyticsOverviewResponse(94.8, 87.5, 12.4, 0.04, "HEALTHY"));

        ResponseEntity<ObservabilityService.AnalyticsOverviewResponse> response = controller.getOverview(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("HEALTHY", response.getBody().systemHealth());
    }

    @Test
    void testGetDoraMetricsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getDoraMetricsForProject(projectId))
                .thenReturn(new ObservabilityService.DoraMetricsResponse(12.4, 1.5, 4.2, 18.0, "Elite"));

        ResponseEntity<ObservabilityService.DoraMetricsResponse> response = controller.getDoraMetrics(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Elite", response.getBody().performanceTier());
    }
}
