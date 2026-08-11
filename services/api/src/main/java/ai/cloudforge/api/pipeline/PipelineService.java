package ai.cloudforge.api.pipeline;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class PipelineService {

    private final PipelineRepository pipelineRepository;
    private final PipelineRunRepository runRepository;
    private final PipelineStageRepository stageRepository;
    private final EventPublisher eventPublisher;

    public PipelineService(
            PipelineRepository pipelineRepository,
            PipelineRunRepository runRepository,
            PipelineStageRepository stageRepository,
            EventPublisher eventPublisher) {
        this.pipelineRepository = pipelineRepository;
        this.runRepository = runRepository;
        this.stageRepository = stageRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<PipelineResponse> getPipelinesForProject(UUID projectId) {
        return pipelineRepository.findByProjectId(projectId).stream()
                .map(PipelineResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public PipelineResponse getPipelineById(UUID pipelineId) {
        return pipelineRepository.findById(pipelineId)
                .map(PipelineResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline not found"));
    }

    @Transactional
    public PipelineResponse createPipeline(UUID orgId, UUID userId, UUID projectId, UUID repositoryId, String name, String description, String yamlDefinition) {
        Pipeline pipeline = pipelineRepository.save(new Pipeline(projectId, repositoryId, name, description, yamlDefinition));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "PIPELINE_CREATED",
                name,
                "Pipeline " + name + " created"
        ));

        return PipelineResponse.fromEntity(pipeline);
    }

    @Transactional
    public RunResponse triggerRun(UUID orgId, UUID userId, UUID pipelineId, String triggeredBy) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline not found"));

        int nextRunNumber = runRepository.findByPipelineIdOrderByCreatedAtDesc(pipelineId).size() + 1;
        PipelineRun run = runRepository.save(new PipelineRun(pipelineId, nextRunNumber, triggeredBy != null ? triggeredBy : "manual"));

        // Setup DAG stages: Build, Test, Deploy (Protected stage)
        stageRepository.save(new PipelineStage(run.getId(), "Build", 1, false));
        stageRepository.save(new PipelineStage(run.getId(), "Test", 2, false));
        stageRepository.save(new PipelineStage(run.getId(), "Deploy", 3, true));

        run.setStatus("RUNNING");
        PipelineRun saved = runRepository.save(run);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "PIPELINE_TRIGGERED",
                pipeline.getName() + " #" + nextRunNumber,
                "Pipeline run #" + nextRunNumber + " triggered"
        ));

        return RunResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RunResponse> getRunsForPipeline(UUID pipelineId) {
        return runRepository.findByPipelineIdOrderByCreatedAtDesc(pipelineId).stream()
                .map(RunResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RunDetailResponse getRunDetail(UUID runId) {
        PipelineRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline run not found"));
        List<StageResponse> stages = stageRepository.findByPipelineRunIdOrderByStageOrderAsc(runId).stream()
                .map(StageResponse::fromEntity)
                .toList();

        return new RunDetailResponse(RunResponse.fromEntity(run), stages);
    }

    @Transactional
    public RunResponse cancelRun(UUID orgId, UUID userId, UUID runId) {
        PipelineRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline run not found"));

        run.setStatus("CANCELLED");
        PipelineRun saved = runRepository.save(run);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "PIPELINE_CANCELLED",
                "Run #" + run.getRunNumber(),
                "Pipeline run #" + run.getRunNumber() + " cancelled"
        ));

        return RunResponse.fromEntity(saved);
    }

    @Transactional
    public RunResponse approveRun(UUID orgId, UUID userId, UUID runId) {
        PipelineRun run = runRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Pipeline run not found"));

        List<PipelineStage> stages = stageRepository.findByPipelineRunIdOrderByStageOrderAsc(runId);
        stages.stream()
                .filter(PipelineStage::isRequiresApproval)
                .forEach(s -> {
                    s.approve();
                    stageRepository.save(s);
                });

        run.setStatus("SUCCEEDED");
        PipelineRun saved = runRepository.save(run);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "PIPELINE_COMPLETED",
                "Run #" + run.getRunNumber(),
                "Pipeline run #" + run.getRunNumber() + " approved & completed successfully"
        ));

        return RunResponse.fromEntity(saved);
    }

    public record PipelineResponse(
            UUID id,
            UUID projectId,
            UUID repositoryId,
            String name,
            String description,
            String yamlDefinition,
            String status
    ) {
        public static PipelineResponse fromEntity(Pipeline p) {
            return new PipelineResponse(p.getId(), p.getProjectId(), p.getRepositoryId(), p.getName(), p.getDescription(), p.getYamlDefinition(), p.getStatus());
        }
    }

    public record RunResponse(
            UUID id,
            UUID pipelineId,
            Integer runNumber,
            String status,
            String triggeredBy,
            UUID correlationId
    ) {
        public static RunResponse fromEntity(PipelineRun r) {
            return new RunResponse(r.getId(), r.getPipelineId(), r.getRunNumber(), r.getStatus(), r.getTriggeredBy(), r.getCorrelationId());
        }
    }

    public record StageResponse(
            UUID id,
            String name,
            Integer stageOrder,
            String status,
            boolean requiresApproval,
            boolean isApproved
    ) {
        public static StageResponse fromEntity(PipelineStage s) {
            return new StageResponse(s.getId(), s.getName(), s.getStageOrder(), s.getStatus(), s.isRequiresApproval(), s.isApproved());
        }
    }

    public record RunDetailResponse(
            RunResponse run,
            List<StageResponse> stages
    ) {}
}
