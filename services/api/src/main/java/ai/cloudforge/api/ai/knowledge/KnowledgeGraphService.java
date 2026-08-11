package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class KnowledgeGraphService {

    public List<GraphLink> buildKnowledgeGraph(UUID projectId) {
        return List.of(
                new GraphLink("Incident#INC-802", "RootCause#OOMKilled", "HAS_ROOT_CAUSE"),
                new GraphLink("RootCause#OOMKilled", "Runbook#RB-102", "SOLVED_BY"),
                new GraphLink("Runbook#RB-102", "Deployment#v1.4", "APPLIES_TO"),
                new GraphLink("Deployment#v1.4", "Environment#Staging", "TARGETS"),
                new GraphLink("Environment#Staging", "Repository#CloudForge", "BOUND_TO"),
                new GraphLink("Repository#CloudForge", "Prediction#OperationalRisk", "PREDICTED_BY")
        );
    }

    public record GraphLink(
            String source,
            String target,
            String relationship
    ) {}
}
