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
@Table(name = "ai_postmortems")
public class Postmortem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "incident_id")
    private UUID incidentId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(name = "root_cause", nullable = false, columnDefinition = "TEXT")
    private String rootCause;

    @Column(name = "lessons_learned", nullable = false, columnDefinition = "TEXT")
    private String lessonsLearned;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Postmortem() {
    }

    public Postmortem(UUID projectId, UUID incidentId, String summary, String rootCause, String lessonsLearned) {
        this.projectId = projectId;
        this.incidentId = incidentId;
        this.summary = summary;
        this.rootCause = rootCause;
        this.lessonsLearned = lessonsLearned;
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

    public String getSummary() {
        return summary;
    }

    public String getRootCause() {
        return rootCause;
    }

    public String getLessonsLearned() {
        return lessonsLearned;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
