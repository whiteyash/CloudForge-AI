package ai.cloudforge.api.ai.knowledge;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.ai.core.AIResponse;
import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class KnowledgeController {

    private final KnowledgeService service;
    private final RunbookRecommendationService runbookRecommendationService;

    public KnowledgeController(KnowledgeService service, RunbookRecommendationService runbookRecommendationService) {
        this.service = service;
        this.runbookRecommendationService = runbookRecommendationService;
    }

    @GetMapping("/projects/{projectId}/knowledge/search")
    public ResponseEntity<List<KnowledgeSearchService.SearchResult>> searchKnowledge(
            @PathVariable UUID projectId,
            @RequestParam String query) {
        return ResponseEntity.ok(service.searchKnowledge(projectId, query));
    }

    @GetMapping("/projects/{projectId}/runbooks")
    public ResponseEntity<List<KnowledgeService.AIRunbookResponse>> listRunbooks(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getRunbooksForProject(projectId));
    }

    @PostMapping("/projects/{projectId}/runbooks/recommend")
    public ResponseEntity<RunbookRecommendationService.RecommendedRunbook> recommendRunbook(
            @PathVariable UUID projectId,
            @RequestParam String incidentType) {
        return ResponseEntity.ok(runbookRecommendationService.recommendRunbook(incidentType));
    }

    @GetMapping("/projects/{projectId}/incidents/{incidentId}/similar")
    public ResponseEntity<List<IncidentSimilarityService.SimilarIncidentResult>> getSimilarIncidents(
            @PathVariable UUID projectId,
            @PathVariable UUID incidentId) {
        return ResponseEntity.ok(service.getSimilarIncidents(projectId, incidentId));
    }

    @PostMapping("/projects/{projectId}/postmortems/generate")
    public ResponseEntity<AIResponse<KnowledgeService.PostmortemResponse>> generatePostmortem(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID projectId,
            @RequestParam UUID orgId,
            @RequestParam(required = false) UUID incidentId) {
        return ResponseEntity.ok(service.generatePostmortemReport(orgId, principal.userId(), projectId, incidentId));
    }

    @GetMapping("/projects/{projectId}/knowledge/graph")
    public ResponseEntity<List<KnowledgeGraphService.GraphLink>> getKnowledgeGraph(
            @PathVariable UUID projectId) {
        return ResponseEntity.ok(service.getKnowledgeGraph(projectId));
    }
}
