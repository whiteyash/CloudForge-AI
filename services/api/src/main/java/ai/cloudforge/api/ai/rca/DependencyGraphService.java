package ai.cloudforge.api.ai.rca;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class DependencyGraphService {

    public List<DependencyLinkResponse> buildGraph(UUID projectId) {
        return List.of(
                new DependencyLinkResponse("Pipeline#44", "Job#BuildAndTest", "TRIGGERED"),
                new DependencyLinkResponse("Job#BuildAndTest", "Runner#us-east-1a", "EXECUTED_ON"),
                new DependencyLinkResponse("Runner#us-east-1a", "Deployment#v1.4", "DEPLOYED_TO"),
                new DependencyLinkResponse("Deployment#v1.4", "Environment#Staging", "TARGETS"),
                new DependencyLinkResponse("Environment#Staging", "Incident#INC-802", "TRIGGERED_INCIDENT"),
                new DependencyLinkResponse("Incident#INC-802", "RootCause#OOMKilled", "CAUSED_BY")
        );
    }

    public record DependencyLinkResponse(
            String sourceComponent,
            String targetComponent,
            String linkType
    ) {}
}
