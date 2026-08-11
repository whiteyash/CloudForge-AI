package ai.cloudforge.api.ai.prediction;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "capacity_forecasts")
public class CapacityForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "current_usage", nullable = false)
    private double currentUsage;

    @Column(name = "projected_usage", nullable = false)
    private double projectedUsage;

    @Column(name = "exhaustion_days", nullable = false)
    private int exhaustionDays;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CapacityForecast() {
    }

    public CapacityForecast(UUID projectId, String metricName, double currentUsage, double projectedUsage, int exhaustionDays) {
        this.projectId = projectId;
        this.metricName = metricName;
        this.currentUsage = currentUsage;
        this.projectedUsage = projectedUsage;
        this.exhaustionDays = exhaustionDays;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getMetricName() {
        return metricName;
    }

    public double getCurrentUsage() {
        return currentUsage;
    }

    public double getProjectedUsage() {
        return projectedUsage;
    }

    public int getExhaustionDays() {
        return exhaustionDays;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
