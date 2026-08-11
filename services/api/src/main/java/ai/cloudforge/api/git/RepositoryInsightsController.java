package ai.cloudforge.api.git;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RepositoryInsightsController {

    private final RepositoryInsightsService service;

    public RepositoryInsightsController(RepositoryInsightsService service) {
        this.service = service;
    }

    @GetMapping("/repositories/{repositoryId}/insights")
    public ResponseEntity<RepositoryInsightsService.InsightsSummary> getInsights(
            @PathVariable UUID repositoryId) {
        return ResponseEntity.ok(service.getInsightsForRepository(repositoryId));
    }
}
