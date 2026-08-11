package ai.cloudforge.api.ai.knowledge;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class PostmortemService {

    public GeneratedPostmortem generatePostmortem(UUID projectId, UUID incidentId) {
        return new GeneratedPostmortem(
                "Executive Postmortem Report for Incident #" + (incidentId != null ? incidentId : "INC-802"),
                "OOMKilled daemon eviction during parallel image extraction in container stage.",
                "Automate workspace cache pruning and enforce memory limits on runner daemon pool.",
                "1. Increase runner RAM\n2. Add automated monitoring alert for 90% memory threshold"
        );
    }

    public record GeneratedPostmortem(
            String summary,
            String rootCause,
            String lessonsLearned,
            String preventiveActions
    ) {}
}
