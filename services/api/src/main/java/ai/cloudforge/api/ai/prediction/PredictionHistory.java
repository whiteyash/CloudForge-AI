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
@Table(name = "prediction_history")
public class PredictionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "prediction_type", nullable = false, length = 50)
    private String predictionType;

    @Column(name = "actual_outcome", nullable = false, length = 50)
    private String actualOutcome;

    @Column(name = "accuracy_score", nullable = false)
    private int accuracyScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PredictionHistory() {
    }

    public PredictionHistory(UUID projectId, String predictionType, String actualOutcome, int accuracyScore) {
        this.projectId = projectId;
        this.predictionType = predictionType;
        this.actualOutcome = actualOutcome;
        this.accuracyScore = accuracyScore;
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

    public String getPredictionType() {
        return predictionType;
    }

    public String getActualOutcome() {
        return actualOutcome;
    }

    public int getAccuracyScore() {
        return accuracyScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
