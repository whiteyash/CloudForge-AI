package ai.cloudforge.api.ai.prediction;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CapacityForecastServiceTest {

    private CapacityForecastService service;

    @BeforeEach
    void setUp() {
        service = new CapacityForecastService();
    }

    @Test
    void testForecastCapacity() {
        List<CapacityForecastService.CapacityForecastResult> results = service.forecastCapacity(UUID.randomUUID());
        assertNotNull(results);
        assertEquals(3, results.size());
        assertEquals("STORAGE", results.get(0).metricName());
    }
}
