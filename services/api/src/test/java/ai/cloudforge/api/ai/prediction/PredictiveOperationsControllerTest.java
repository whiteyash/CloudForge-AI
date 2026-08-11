package ai.cloudforge.api.ai.prediction;

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

class PredictiveOperationsControllerTest {

    private PredictiveOperationsService service;
    private PredictiveOperationsController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(PredictiveOperationsService.class);
        controller = new PredictiveOperationsController(service);
    }

    @Test
    void testListPredictionsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getForecastsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<PredictiveOperationsService.PredictiveForecastResponse>> response = controller.listPredictions(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testGetCapacityEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getCapacityForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<CapacityForecastService.CapacityForecastResult>> response = controller.getCapacity(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
