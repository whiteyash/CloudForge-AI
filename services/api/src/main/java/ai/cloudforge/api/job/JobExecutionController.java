package ai.cloudforge.api.job;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.cloudforge.api.auth.AuthPrincipal;

@RestController
@RequestMapping
public class JobExecutionController {

    private final JobExecutionService service;

    public JobExecutionController(JobExecutionService service) {
        this.service = service;
    }

    @GetMapping("/pipeline-runs/{runId}/jobs")
    public ResponseEntity<List<JobExecutionService.JobResponse>> getJobsForRun(
            @PathVariable UUID runId) {
        return ResponseEntity.ok(service.getJobsForRun(runId));
    }

    @GetMapping("/job-executions/{jobId}")
    public ResponseEntity<JobExecutionService.JobResponse> getJobById(
            @PathVariable UUID jobId) {
        return ResponseEntity.ok(service.getJobById(jobId));
    }

    @GetMapping("/job-executions/{jobId}/logs")
    public ResponseEntity<List<JobExecutionService.LogResponse>> getLogsForJob(
            @PathVariable UUID jobId) {
        return ResponseEntity.ok(service.getLogsForJob(jobId));
    }

    @PostMapping("/job-executions/{jobId}/logs")
    public ResponseEntity<JobExecutionService.LogResponse> appendLog(
            @PathVariable UUID jobId,
            @RequestBody AppendLogRequest request) {
        return ResponseEntity.ok(service.appendLog(jobId, request.logLine(), request.streamType()));
    }

    @PostMapping("/job-executions/{jobId}/cancel")
    public ResponseEntity<JobExecutionService.JobResponse> cancelJob(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID jobId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.cancelJob(orgId, principal.userId(), jobId));
    }

    @PostMapping("/job-executions/{jobId}/retry")
    public ResponseEntity<JobExecutionService.JobResponse> retryJob(
            @AuthenticationPrincipal AuthPrincipal principal,
            @PathVariable UUID jobId,
            @RequestParam UUID orgId) {
        return ResponseEntity.ok(service.retryJob(orgId, principal.userId(), jobId));
    }

    public record AppendLogRequest(
            String logLine,
            String streamType
    ) {}
}
