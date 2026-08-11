package ai.cloudforge.api.ai.knowledge;

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
@Table(name = "incident_similarity")
public class IncidentSimilarity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "incident_id")
    private UUID incidentId;

    @Column(name = "similar_incident_id")
    private UUID similarIncidentId;

    @Column(name = "similarity_score", nullable = false)
    private int similarityScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected IncidentSimilarity() {
    }

    public IncidentSimilarity(UUID projectId, UUID incidentId, UUID similarIncidentId, int similarityScore) {
        this.projectId = projectId;
        this.incidentId = incidentId;
        this.similarIncidentId = similarIncidentId;
        this.similarityScore = similarityScore;
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

    public UUID getIncidentId() {
        return incidentId;
    }

    public UUID getSimilarIncidentId() {
        return similarIncidentId;
    }

    public int getSimilarityScore() {
        return similarityScore;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
