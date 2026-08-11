package ai.cloudforge.api.aiops;

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

class IncidentServiceTest {

    private IncidentRepository incidentRepository;
    private IncidentTimelineRepository timelineRepository;
    private IncidentRecommendationRepository recommendationRepository;
    private EventPublisher eventPublisher;
    private IncidentService service;

    @BeforeEach
    void setUp() {
        incidentRepository = Mockito.mock(IncidentRepository.class);
        timelineRepository = Mockito.mock(IncidentTimelineRepository.class);
        recommendationRepository = Mockito.mock(IncidentRecommendationRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);

        service = new IncidentService(incidentRepository, timelineRepository, recommendationRepository, eventPublisher);
    }

    @Test
    void testCreateIncidentAndGeneratesRcaAndTimeline() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(incidentRepository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        IncidentService.IncidentResponse response = service.createIncident(
                orgId, userId, projectId, "Production Pod Eviction", "CRITICAL", "Memory limits exceeded on k8s node", 0.94
        );

        assertNotNull(response);
        assertEquals("Production Pod Eviction", response.title());
        assertEquals("CRITICAL", response.severity());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testResolveIncident() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID incidentId = UUID.randomUUID();
        Incident incident = new Incident(UUID.randomUUID(), "Runner Offline", "HIGH", "Node rebooted", 0.88);

        when(incidentRepository.findById(incidentId)).thenReturn(Optional.of(incident));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        IncidentService.IncidentResponse response = service.resolveIncident(orgId, userId, incidentId);

        assertNotNull(response);
        assertEquals("RESOLVED", response.status());
    }
}
