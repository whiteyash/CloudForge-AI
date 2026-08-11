package ai.cloudforge.api.k8s;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
import org.springframework.test.web.servlet.MockMvc;

import ai.cloudforge.api.auth.JwtAuthenticationFilter;
import ai.cloudforge.api.k8s.K8sDtos.ClusterResponseDto;
import ai.cloudforge.api.k8s.K8sDtos.K8sClusterSummaryDto;

@WebMvcTest(controllers = K8sClusterController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class K8sClusterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private K8sClusterService k8sClusterService;

    @Test
    @DisplayName("GET /k8s/summary should return cluster summary metrics")
    void getSummary_Success() throws Exception {
        K8sClusterSummaryDto summary = new K8sClusterSummaryDto(
                2, "100% HEALTHY", 10, 0, 30.0, 8, 45.0, 16, Instant.now().toString(), List.of()
        );
        when(k8sClusterService.getClusterSummary(any(), any())).thenReturn(summary);

        mockMvc.perform(get("/k8s/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeClusters").value(2))
                .andExpect(jsonPath("$.healthStatus").value("100% HEALTHY"))
                .andExpect(jsonPath("$.runningPods").value(10));
    }

    @Test
    @DisplayName("GET /projects/{projectId}/clusters should return project cluster list")
    void listClusters_Success() throws Exception {
        UUID projectId = UUID.randomUUID();
        ClusterResponseDto cluster = new ClusterResponseDto(
                UUID.randomUUID(), projectId, UUID.randomUUID(), "prod-cluster", "EKS", "https://eks.amazonaws.com", "PRODUCTION", "CONNECTED", 8, 3, 25.0, 40.0, Instant.now(), Instant.now(), Instant.now()
        );
        when(k8sClusterService.listClusters(eq(projectId), any())).thenReturn(List.of(cluster));

        mockMvc.perform(get("/projects/{projectId}/clusters", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("prod-cluster"))
                .andExpect(jsonPath("$[0].provider").value("EKS"));
    }

    @Test
    @DisplayName("DELETE /projects/{projectId}/clusters/{id} should disconnect cluster")
    void disconnectCluster_Success() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID clusterId = UUID.randomUUID();
        doNothing().when(k8sClusterService).disconnectCluster(eq(projectId), eq(clusterId), any());

        mockMvc.perform(delete("/projects/{projectId}/clusters/{id}", projectId, clusterId))
                .andExpect(status().isNoContent());
    }
}
