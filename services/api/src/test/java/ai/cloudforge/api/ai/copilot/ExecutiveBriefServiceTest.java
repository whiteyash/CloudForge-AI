package ai.cloudforge.api.ai.copilot;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

import ai.cloudforge.api.ai.memory.AIConversationResponse;

class ExecutiveBriefServiceTest {

    private ExecutiveBriefRepository briefRepository;
    private ExecutiveBriefService service;

    @BeforeEach
    void setUp() {
        briefRepository = Mockito.mock(ExecutiveBriefRepository.class);
        service = new ExecutiveBriefService(briefRepository);
    }

    @Test
    void testGenerateExecutiveBrief() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(briefRepository.save(any(ExecutiveBrief.class))).thenAnswer(inv -> inv.getArgument(0));

        AIConversationResponse<ExecutiveBriefService.ExecutiveBriefResponse> response = service.generateExecutiveBrief(projectId, userId, "DAILY");

        assertNotNull(response);
        assertEquals(98, response.baseResponse().confidence());
        assertNotNull(response.baseResponse().payload());
    }
}
