package ai.cloudforge.api.ai.copilot;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.ai.memory.AIConversationResponse;

@Service
public class ExecutiveBriefService {

    private final ExecutiveBriefRepository briefRepository;

    public ExecutiveBriefService(ExecutiveBriefRepository briefRepository) {
        this.briefRepository = briefRepository;
    }

    @Transactional
    public AIConversationResponse<ExecutiveBriefResponse> generateExecutiveBrief(UUID projectId, UUID userId, String periodType) {
        String summary = "Executive Operations Brief (" + (periodType != null ? periodType : "DAILY") + "): All 12 production services healthy. DORA deployment frequency up 14%. 0 high-severity incidents active.";

        ExecutiveBrief brief = briefRepository.save(new ExecutiveBrief(
                projectId, userId, periodType, summary
        ));

        ExecutiveBriefResponse payload = ExecutiveBriefResponse.fromEntity(brief);

        AIResponse<ExecutiveBriefResponse> baseResponse = new AIResponse<>(
                brief.getSummaryText(),
                98,
                List.of("12 healthy services", "0 active high-severity incidents"),
                "None",
                List.of("Maintain current deployment pipeline concurrency"),
                List.of("Review weekly capacity forecasts"),
                List.of("Brief#" + brief.getId()),
                payload
        );

        return new AIConversationResponse<>(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                "Daily Operations Brief",
                baseResponse
        );
    }

    public record ExecutiveBriefResponse(
            UUID id,
            UUID projectId,
            UUID userId,
            String periodType,
            String summaryText
    ) {
        public static ExecutiveBriefResponse fromEntity(ExecutiveBrief b) {
            return new ExecutiveBriefResponse(b.getId(), b.getProjectId(), b.getUserId(), b.getPeriodType(), b.getSummaryText());
        }
    }
}
