package ai.cloudforge.api.aiops;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ai.cloudforge.api.aiops.IncidentDtos.IncidentResponseDto;
import ai.cloudforge.api.aiops.IncidentDtos.IncidentSummaryDto;
import ai.cloudforge.api.aiops.IncidentDtos.TriggerIncidentRequest;
import ai.cloudforge.api.auth.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = IncidentResponseController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class IncidentResponseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private IncidentResponseService incidentService;

    @Test
    @DisplayName("GET /api/incidents/summary should return incident summary metrics")
    void getGlobalSummary_Success() throws Exception {
        IncidentSummaryDto summary = new IncidentSummaryDto(
                10, 3, 1, 2, 4, 3, 4.5, 18.2, List.of(), List.of()
        );
        when(incidentService.getIncidentSummary(any(), any())).thenReturn(summary);

        mockMvc.perform(get("/api/incidents/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIncidents").value(10))
                .andExpect(jsonPath("$.openIncidents").value(3))
                .andExpect(jsonPath("$.sev1Count").value(1));
    }

    @Test
    @DisplayName("POST /projects/{projectId}/incidents should trigger new incident")
    void triggerIncident_Success() throws Exception {
        UUID projectId = UUID.randomUUID();
        IncidentResponseDto incident = new IncidentResponseDto(
                UUID.randomUUID(), projectId, UUID.randomUUID(), "Service Down", "Pod CrashLoopBackOff", "SEV_1_CRITICAL", "TRIGGERED", null, "PROMETHEUS", Instant.now(), null, null
        );
        when(incidentService.triggerIncident(eq(projectId), any(), any())).thenReturn(incident);

        TriggerIncidentRequest request = new TriggerIncidentRequest("Service Down", "Pod CrashLoopBackOff", "SEV_1_CRITICAL", "PROMETHEUS");

        mockMvc.perform(post("/projects/{projectId}/incidents", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Service Down"))
                .andExpect(jsonPath("$.severity").value("SEV_1_CRITICAL"));
    }
}
