package ai.cloudforge.api.aiops;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "incident_recommendations")
public class IncidentRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "incident_id", nullable = false)
    private UUID incidentId;

    @Column(name = "action_recommended", nullable = false, length = 255)
    private String actionRecommended;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "confidence_score")
    private Double confidenceScore = 0.90;

    protected IncidentRecommendation() {
    }

    public IncidentRecommendation(UUID incidentId, String actionRecommended, String reasoning, Double confidenceScore) {
        this.incidentId = incidentId;
        this.actionRecommended = actionRecommended;
        this.reasoning = reasoning;
        this.confidenceScore = confidenceScore != null ? confidenceScore : 0.90;
    }

    public UUID getId() {
        return id;
    }

    public UUID getIncidentId() {
        return incidentId;
    }

    public String getActionRecommended() {
        return actionRecommended;
    }

    public String getReasoning() {
        return reasoning;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }
}
