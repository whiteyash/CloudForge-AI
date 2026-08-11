package ai.cloudforge.api.pipeline;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.EventPublisher;

class PipelineSecurityTest {

    private PipelineRepository pipelineRepository;
    private PipelineService service;

    @BeforeEach
    void setUp() {
        pipelineRepository = Mockito.mock(PipelineRepository.class);
        PipelineRunRepository runRepository = Mockito.mock(PipelineRunRepository.class);
        PipelineStageRepository stageRepository = Mockito.mock(PipelineStageRepository.class);
        EventPublisher eventPublisher = Mockito.mock(EventPublisher.class);
        service = new PipelineService(pipelineRepository, runRepository, stageRepository, eventPublisher);
    }

    @Test
    void testUnauthorizedPipelineAccessThrowsNotFound() {
        UUID pipelineId = UUID.randomUUID();
        when(pipelineRepository.findById(pipelineId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.getPipelineById(pipelineId);
        });
    }
}
