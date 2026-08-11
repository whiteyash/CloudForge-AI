package ai.cloudforge.api.aiops;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AIChatService {

    private final AiOperationHistoryRepository historyRepository;

    public AIChatService(AiOperationHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public ChatResponse processQuery(UUID projectId, UUID userId, String prompt) {
        String responseText;
        String lower = prompt.toLowerCase();

        if (lower.contains("fail")) {
            responseText = "Deployment #dep-881 failed due to a runner agent disconnect during container extraction. Pre-flight risk was LOW, but node memory spike caused container runtime eviction.";
        } else if (lower.contains("runner") || lower.contains("utilization")) {
            responseText = "Runner pool capacity is currently at 87.5% across 4 active nodes. 1 node is in DRAIN mode for maintenance.";
        } else if (lower.contains("history") || lower.contains("dora")) {
            responseText = "DORA Performance Tier is ELITE. Deployment Frequency is 12.4/day, Lead Time is 1.5 hours, Change Failure Rate is 4.2%, and MTTR is 18 minutes.";
        } else {
            responseText = "CloudForge AI Mission Control Assistant: System health is HEALTHY. All 5 pipeline stages and deployment gates are operational.";
        }

        historyRepository.save(new AiOperationHistory(projectId, userId, prompt, responseText));

        return new ChatResponse(prompt, responseText);
    }

    public record ChatResponse(
            String prompt,
            String response
    ) {}
}
