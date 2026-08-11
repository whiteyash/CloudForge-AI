package ai.cloudforge.api.aiops;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class AIOpsSecurityTest {

    private IncidentRepository incidentRepository;
    private IncidentService service;

    @BeforeEach
    void setUp() {
        incidentRepository = Mockito.mock(IncidentRepository.class);
        IncidentTimelineRepository timelineRepository = Mockito.mock(IncidentTimelineRepository.class);
        IncidentRecommendationRepository recommendationRepository = Mockito.mock(IncidentRecommendationRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new IncidentService(incidentRepository, timelineRepository, recommendationRepository, eventPublisher);
    }

    @Test
    void testUnauthorizedIncidentAccessThrowsNotFound() {
        UUID incidentId = UUID.randomUUID();
        when(incidentRepository.findById(incidentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getIncidentById(incidentId);
        });
    }
}
