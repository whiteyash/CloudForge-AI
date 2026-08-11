package ai.cloudforge.api.observability;

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
@Table(name = "metric_snapshots")
public class MetricSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Column(name = "metric_value", nullable = false)
    private Double metricValue;

    @Column(name = "aggregation_period", length = 30)
    private String aggregationPeriod = "DAILY";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected MetricSnapshot() {
    }

    public MetricSnapshot(UUID projectId, String metricName, Double metricValue, String aggregationPeriod) {
        this.projectId = projectId;
        this.metricName = metricName;
        this.metricValue = metricValue;
        this.aggregationPeriod = aggregationPeriod != null ? aggregationPeriod : "DAILY";
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

    public Double getMetricValue() {
        return metricValue;
    }

    public String getAggregationPeriod() {
        return aggregationPeriod;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
