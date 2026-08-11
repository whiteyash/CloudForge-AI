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
@Table(name = "predictive_forecasts")
public class PredictiveForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "forecast_type", nullable = false, length = 50)
    private String forecastType;

    @Column(name = "probability_score", nullable = false)
    private int probabilityScore;

    @Column(name = "confidence_score", nullable = false)
    private int confidenceScore;

    @Column(name = "forecast_window", nullable = false, length = 50)
    private String forecastWindow;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PredictiveForecast() {
    }

    public PredictiveForecast(UUID projectId, String forecastType, int probabilityScore, int confidenceScore, String forecastWindow) {
        this.projectId = projectId;
        this.forecastType = forecastType;
        this.probabilityScore = probabilityScore;
        this.confidenceScore = confidenceScore;
        this.forecastWindow = forecastWindow;
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

    public String getForecastType() {
        return forecastType;
    }

    public int getProbabilityScore() {
        return probabilityScore;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public String getForecastWindow() {
        return forecastWindow;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
