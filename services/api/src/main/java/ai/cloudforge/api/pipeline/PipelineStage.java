package ai.cloudforge.api.pipeline;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pipeline_stages")
public class PipelineStage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pipeline_run_id", nullable = false)
    private UUID pipelineRunId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(length = 30)
    private String status = "PENDING";

    @Column(name = "requires_approval")
    private boolean requiresApproval = false;

    @Column(name = "is_approved")
    private boolean isApproved = false;

    protected PipelineStage() {
    }

    public PipelineStage(UUID pipelineRunId, String name, Integer stageOrder, boolean requiresApproval) {
        this.pipelineRunId = pipelineRunId;
        this.name = name;
        this.stageOrder = stageOrder;
        this.requiresApproval = requiresApproval;
        this.status = requiresApproval ? "PENDING_APPROVAL" : "PENDING";
    }

    public UUID getId() {
        return id;
    }

    public UUID getPipelineRunId() {
        return pipelineRunId;
    }

    public String getName() {
        return name;
    }

    public Integer getStageOrder() {
        return stageOrder;
    }

    public String getStatus() {
        return status;
    }

    public boolean isRequiresApproval() {
        return requiresApproval;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void approve() {
        this.isApproved = true;
        this.status = "RUNNING";
    }
}
