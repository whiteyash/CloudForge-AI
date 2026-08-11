package ai.cloudforge.api.pipeline;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

class PipelineServiceTest {

    private PipelineRepository pipelineRepository;
    private PipelineRunRepository runRepository;
    private PipelineStageRepository stageRepository;
    private EventPublisher eventPublisher;
    private PipelineService service;

    @BeforeEach
    void setUp() {
        pipelineRepository = Mockito.mock(PipelineRepository.class);
        runRepository = Mockito.mock(PipelineRunRepository.class);
        stageRepository = Mockito.mock(PipelineStageRepository.class);
        eventPublisher = Mockito.mock(EventPublisher.class);
        service = new PipelineService(pipelineRepository, runRepository, stageRepository, eventPublisher);
    }

    @Test
    void testCreatePipeline() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();

        when(pipelineRepository.save(any(Pipeline.class))).thenAnswer(inv -> inv.getArgument(0));

        PipelineService.PipelineResponse response = service.createPipeline(
                orgId, userId, projectId, null, "ci-build", "Build & Test Pipeline", "name: ci-build"
        );

        assertNotNull(response);
        assertEquals("ci-build", response.name());
        verify(eventPublisher).publishEvent(any(CloudForgeEvent.class));
    }

    @Test
    void testTriggerRunAndApproval() {
        UUID orgId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID pipelineId = UUID.randomUUID();
        Pipeline pipeline = new Pipeline(UUID.randomUUID(), null, "cd-deploy", "Deploy Pipeline", "name: cd-deploy");

        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.of(pipeline));
        when(runRepository.findByPipelineIdOrderByCreatedAtDesc(pipelineId)).thenReturn(List.of());
        when(runRepository.save(any(PipelineRun.class))).thenAnswer(inv -> inv.getArgument(0));

        PipelineService.RunResponse runResponse = service.triggerRun(orgId, userId, pipelineId, "manual");

        assertNotNull(runResponse);
        assertEquals("RUNNING", runResponse.status());

        // Approve run
        PipelineRun run = new PipelineRun(pipelineId, 1, "manual");
        when(runRepository.findById(run.getId())).thenReturn(Optional.of(run));
        when(stageRepository.findByPipelineRunIdOrderByStageOrderAsc(run.getId())).thenReturn(List.of(
                new PipelineStage(run.getId(), "Deploy", 3, true)
        ));

        PipelineService.RunResponse approvedResponse = service.approveRun(orgId, userId, run.getId());
        assertEquals("SUCCEEDED", approvedResponse.status());
    }
}
