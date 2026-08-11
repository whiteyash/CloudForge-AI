package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class IncidentSimilarityService {

    public List<SimilarIncidentResult> findSimilarIncidents(UUID projectId, UUID incidentId) {
        return List.of(
                new SimilarIncidentResult("INC-704", "Runner Daemon Memory Pressure", 94, "Resolved by expanding runner RAM to 4GB", "18 minutes"),
                new SimilarIncidentResult("INC-612", "Pipeline Stage OOM Eviction", 86, "Resolved by clearing build workspace cache", "24 minutes")
        );
    }

    public record SimilarIncidentResult(
            String incidentCode,
            String title,
            int similarityPercent,
            String historicalResolution,
            String resolutionDuration
    ) {}
}
