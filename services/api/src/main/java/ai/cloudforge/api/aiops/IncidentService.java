package ai.cloudforge.api.aiops;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final IncidentTimelineRepository timelineRepository;
    private final IncidentRecommendationRepository recommendationRepository;
    private final EventPublisher eventPublisher;

    public IncidentService(
            IncidentRepository incidentRepository,
            IncidentTimelineRepository timelineRepository,
            IncidentRecommendationRepository recommendationRepository,
            EventPublisher eventPublisher) {
        this.incidentRepository = incidentRepository;
        this.timelineRepository = timelineRepository;
        this.recommendationRepository = recommendationRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> getIncidentsForProject(UUID projectId) {
        return incidentRepository.findByProjectId(projectId).stream()
                .map(IncidentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public IncidentDetailResponse getIncidentById(UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        List<TimelineResponse> timeline = timelineRepository.findByIncidentIdOrderByTimestampAsc(incidentId).stream()
                .map(TimelineResponse::fromEntity)
                .toList();

        List<RecommendationResponse> recommendations = recommendationRepository.findByIncidentId(incidentId).stream()
                .map(RecommendationResponse::fromEntity)
                .toList();

        return new IncidentDetailResponse(IncidentResponse.fromEntity(incident), timeline, recommendations);
    }

    @Transactional
    public IncidentResponse createIncident(
            UUID orgId,
            UUID userId,
            UUID projectId,
            String title,
            String severity,
            String rootCause,
            Double confidenceScore) {

        Incident incident = incidentRepository.save(new Incident(
                projectId, title, severity, rootCause, confidenceScore
        ));

        // Create default timeline events
        timelineRepository.save(new IncidentTimelineEvent(incident.getId(), "Deployment started on production target", "DEPLOYMENT_START"));
        timelineRepository.save(new IncidentTimelineEvent(incident.getId(), "Runner node lost heartbeat ping", "RUNNER_OFFLINE"));
        timelineRepository.save(new IncidentTimelineEvent(incident.getId(), "Job execution timed out after 300 seconds", "JOB_FAILED"));
        timelineRepository.save(new IncidentTimelineEvent(incident.getId(), "AI engine identified root cause & generated rollback recommendation", "RCA_ANALYSIS"));

        // Create default AI recommendations
        recommendationRepository.save(new IncidentRecommendation(
                incident.getId(),
                "Execute Automated Rollback to Previous Release v2.4.1",
                "Runner node disconnect caused partial pod deployment; rolling back ensures zero-downtime stability.",
                0.92
        ));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "INCIDENT_DETECTED",
                title,
                "AI engine detected " + severity + " incident: " + title
        ));

        return IncidentResponse.fromEntity(incident);
    }

    @Transactional
    public IncidentResponse resolveIncident(UUID orgId, UUID userId, UUID incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        incident.resolve();
        Incident saved = incidentRepository.save(incident);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "INCIDENT_RESOLVED",
                saved.getTitle(),
                "Incident " + saved.getTitle() + " resolved successfully"
        ));

        return IncidentResponse.fromEntity(saved);
    }

    public record IncidentResponse(
            UUID id,
            UUID projectId,
            String title,
            String severity,
            String status,
            String rootCause,
            Double confidenceScore
    ) {
        public static IncidentResponse fromEntity(Incident i) {
            return new IncidentResponse(
                    i.getId(), i.getProjectId(), i.getTitle(), i.getSeverity(),
                    i.getStatus(), i.getRootCause(), i.getConfidenceScore()
            );
        }
    }

    public record TimelineResponse(
            UUID id,
            String eventDescription,
            String eventType
    ) {
        public static TimelineResponse fromEntity(IncidentTimelineEvent t) {
            return new TimelineResponse(t.getId(), t.getEventDescription(), t.getEventType());
        }
    }

    public record RecommendationResponse(
            UUID id,
            String actionRecommended,
            String reasoning,
            Double confidenceScore
    ) {
        public static RecommendationResponse fromEntity(IncidentRecommendation r) {
            return new RecommendationResponse(r.getId(), r.getActionRecommended(), r.getReasoning(), r.getConfidenceScore());
        }
    }

    public record IncidentDetailResponse(
            IncidentResponse incident,
            List<TimelineResponse> timeline,
            List<RecommendationResponse> recommendations
    ) {}
}
