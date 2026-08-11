package ai.cloudforge.api.ai.copilot;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class CopilotControllerTest {

    private CopilotService service;
    private ExecutiveBriefService briefService;
    private CopilotController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(CopilotService.class);
        briefService = Mockito.mock(ExecutiveBriefService.class);
        controller = new CopilotController(service, briefService);
    }

    @Test
    void testGetConversationsEndpoint() {
        UUID projectId = UUID.randomUUID();
        when(service.getConversationsForProject(projectId)).thenReturn(List.of());

        ResponseEntity<List<CopilotService.CopilotSessionResponse>> response = controller.getConversations(projectId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testDeleteConversationEndpoint() {
        UUID projectId = UUID.randomUUID();
        UUID conversationId = UUID.randomUUID();

        ResponseEntity<Void> response = controller.deleteConversation(projectId, conversationId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
