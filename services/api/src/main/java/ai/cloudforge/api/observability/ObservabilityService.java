package ai.cloudforge.api.observability;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class ObservabilityService {

    private final MetricSnapshotRepository metricRepository;
    private final SystemAlertRepository alertRepository;
    private final EventPublisher eventPublisher;

    public ObservabilityService(
            MetricSnapshotRepository metricRepository,
            SystemAlertRepository alertRepository,
            EventPublisher eventPublisher) {
        this.metricRepository = metricRepository;
        this.alertRepository = alertRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse getOverviewForProject(UUID projectId) {
        return new AnalyticsOverviewResponse(
                94.8, // Pipeline Success Rate %
                87.5, // Runner Utilization %
                12.4, // Deployments / Day
                0.04, // Change Failure Rate (4%)
                "HEALTHY"
        );
    }

    @Transactional(readOnly = true)
    public DoraMetricsResponse getDoraMetricsForProject(UUID projectId) {
        return new DoraMetricsResponse(
                12.4, // Deployment Frequency (per day)
                1.5,  // Lead Time for Changes (hours)
                4.2,  // Change Failure Rate (%)
                18.0, // Mean Time to Restore Service (MTTR mins)
                "Elite" // DORA Performance Tier
        );
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> getAlertsForProject(UUID projectId) {
        return alertRepository.findByProjectId(projectId).stream()
                .map(AlertResponse::fromEntity)
                .toList();
    }

    @Transactional
    public AlertResponse acknowledgeAlert(UUID orgId, UUID userId, UUID alertId) {
        SystemAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert record not found"));

        alert.acknowledge();
        SystemAlert saved = alertRepository.save(alert);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "ALERT_RESOLVED",
                saved.getAlertName(),
                "System alert " + saved.getAlertName() + " acknowledged"
        ));

        return AlertResponse.fromEntity(saved);
    }

    public record AnalyticsOverviewResponse(
            Double pipelineSuccessRate,
            Double runnerUtilization,
            Double deploymentFrequency,
            Double changeFailureRate,
            String systemHealth
    ) {}

    public record DoraMetricsResponse(
            Double deploymentFrequency,
            Double leadTimeHours,
            Double changeFailureRate,
            Double mttrMinutes,
            String performanceTier
    ) {}

    public record AlertResponse(
            UUID id,
            UUID projectId,
            String alertName,
            String severity,
            String status,
            String message
    ) {
        public static AlertResponse fromEntity(SystemAlert a) {
            return new AlertResponse(a.getId(), a.getProjectId(), a.getAlertName(), a.getSeverity(), a.getStatus(), a.getMessage());
        }
    }
}
