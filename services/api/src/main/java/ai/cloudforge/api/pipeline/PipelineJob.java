package ai.cloudforge.api.pipeline;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pipeline_jobs")
public class PipelineJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "stage_id", nullable = false)
    private UUID stageId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 30)
    private String status = "PENDING";

    protected PipelineJob() {
    }

    public PipelineJob(UUID stageId, String name) {
        this.stageId = stageId;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public UUID getStageId() {
        return stageId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
