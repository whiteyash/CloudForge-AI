package ai.cloudforge.api.observability;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class ObservabilitySecurityTest {

    private SystemAlertRepository alertRepository;
    private ObservabilityService service;

    @BeforeEach
    void setUp() {
        MetricSnapshotRepository metricRepository = Mockito.mock(MetricSnapshotRepository.class);
        alertRepository = Mockito.mock(SystemAlertRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new ObservabilityService(metricRepository, alertRepository, eventPublisher);
    }

    @Test
    void testUnauthorizedAlertAccessThrowsNotFound() {
        UUID alertId = UUID.randomUUID();
        when(alertRepository.findById(alertId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.acknowledgeAlert(UUID.randomUUID(), UUID.randomUUID(), alertId);
        });
    }
}
