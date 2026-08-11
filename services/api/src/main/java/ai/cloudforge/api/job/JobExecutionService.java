package ai.cloudforge.api.job;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class JobExecutionService {

    private final JobExecutionRepository executionRepository;
    private final JobLogRepository logRepository;
    private final EventPublisher eventPublisher;

    public JobExecutionService(
            JobExecutionRepository executionRepository,
            JobLogRepository logRepository,
            EventPublisher eventPublisher) {
        this.executionRepository = executionRepository;
        this.logRepository = logRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<JobResponse> getJobsForRun(UUID pipelineRunId) {
        return executionRepository.findByPipelineRunId(pipelineRunId).stream()
                .map(JobResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public JobResponse getJobById(UUID jobId) {
        return executionRepository.findById(jobId)
                .map(JobResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Job execution not found"));
    }

    @Transactional(readOnly = true)
    public List<LogResponse> getLogsForJob(UUID jobId) {
        return logRepository.findByJobExecutionIdOrderBySequenceNumberAsc(jobId).stream()
                .map(LogResponse::fromEntity)
                .toList();
    }

    @Transactional
    public JobResponse startJob(UUID orgId, UUID userId, UUID pipelineRunId, String jobName, UUID runnerId) {
        JobExecution job = new JobExecution(pipelineRunId, jobName, runnerId);
        job.setStatus("RUNNING");
        JobExecution saved = executionRepository.save(job);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "JOB_STARTED",
                jobName,
                "Job " + jobName + " execution started"
        ));

        return JobResponse.fromEntity(saved);
    }

    @Transactional
    public LogResponse appendLog(UUID jobId, String logLine, String streamType) {
        int nextSeq = logRepository.findByJobExecutionIdOrderBySequenceNumberAsc(jobId).size() + 1;
        JobLog log = logRepository.save(new JobLog(jobId, nextSeq, logLine, streamType));
        return LogResponse.fromEntity(log);
    }

    @Transactional
    public JobResponse completeJob(UUID orgId, UUID userId, UUID jobId, int exitCode) {
        JobExecution job = executionRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job execution not found"));

        job.complete(exitCode);
        JobExecution saved = executionRepository.save(job);

        String eventType = exitCode == 0 ? "JOB_COMPLETED" : "JOB_FAILED";
        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                eventType,
                saved.getJobName(),
                "Job " + saved.getJobName() + " finished with exit code " + exitCode
        ));

        return JobResponse.fromEntity(saved);
    }

    @Transactional
    public JobResponse cancelJob(UUID orgId, UUID userId, UUID jobId) {
        JobExecution job = executionRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job execution not found"));

        job.setStatus("CANCELLED");
        JobExecution saved = executionRepository.save(job);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "JOB_CANCELLED",
                saved.getJobName(),
                "Job " + saved.getJobName() + " execution cancelled"
        ));

        return JobResponse.fromEntity(saved);
    }

    @Transactional
    public JobResponse retryJob(UUID orgId, UUID userId, UUID jobId) {
        JobExecution job = executionRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job execution not found"));

        job.retry();
        JobExecution saved = executionRepository.save(job);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "JOB_STARTED",
                saved.getJobName(),
                "Job " + saved.getJobName() + " retried (Attempt #" + (saved.getRetryCount() + 1) + ")"
        ));

        return JobResponse.fromEntity(saved);
    }

    public record JobResponse(
            UUID id,
            UUID pipelineRunId,
            String jobName,
            UUID runnerId,
            String status,
            Integer exitCode,
            Integer retryCount,
            Long durationMs,
            String failureReason
    ) {
        public static JobResponse fromEntity(JobExecution j) {
            return new JobResponse(
                    j.getId(), j.getPipelineRunId(), j.getJobName(), j.getRunnerId(),
                    j.getStatus(), j.getExitCode(), j.getRetryCount(), j.getDurationMs(), j.getFailureReason()
            );
        }
    }

    public record LogResponse(
            UUID id,
            UUID jobExecutionId,
            Integer sequenceNumber,
            String logLine,
            String streamType
    ) {
        public static LogResponse fromEntity(JobLog l) {
            return new LogResponse(l.getId(), l.getJobExecutionId(), l.getSequenceNumber(), l.getLogLine(), l.getStreamType());
        }
    }
}
