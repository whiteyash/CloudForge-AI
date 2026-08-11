package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class KnowledgeSearchService {

    public List<SearchResult> searchKnowledge(UUID projectId, String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }
        return List.of(
                new SearchResult("RB-101", "PostgreSQL Connection Exhaustion Runbook", "RUNBOOK", 95),
                new SearchResult("KB-404", "Runner Container OOMKilled Mitigation Guide", "ARTICLE", 88)
        );
    }

    public record SearchResult(
            String id,
            String title,
            String type,
            int relevanceScore
    ) {}
}
