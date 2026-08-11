package ai.cloudforge.api.observability;

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

class ObservabilityServiceTest {

    private MetricSnapshotRepository metricRepository;
    private SystemAlertRepository alertRepository;
    private EventPublisher eventPublisher;
    private ObservabilityService service;

    @BeforeEach
    void setUp() {
        metricRepository = Mockito.mock(MetricSnapshotRepository.class);
        alertRepository = Mockito.mock(SystemAlertRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new ObservabilityService(metricRepository, alertRepository, eventPublisher);
    }

    @Test
    void testGetOverviewAndDoraMetrics() {
        UUID projectId = UUID.randomUUID();

        ObservabilityService.AnalyticsOverviewResponse overview = service.getOverviewForProject(projectId);
        assertNotNull(overview);
        assertEquals("HEALTHY", overview.systemHealth());

        ObservabilityService.DoraMetricsResponse dora = service.getDoraMetricsForProject(projectId);
        assertNotNull(dora);
        assertEquals("Elite", dora.performanceTier());
    }

    @Test
    void testAcknowledgeAlert() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID alertId = UUID.randomUUID();
        SystemAlert alert = new SystemAlert(UUID.randomUUID(), "Runner Offline", "WARNING", "Runner agent disconnected");

        when(alertRepository.findById(alertId)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(SystemAlert.class))).thenAnswer(inv -> inv.getArgument(0));

        ObservabilityService.AlertResponse response = service.acknowledgeAlert(orgId, userId, alertId);
        assertNotNull(response);
        assertEquals("ACKNOWLEDGED", response.status());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }
}
