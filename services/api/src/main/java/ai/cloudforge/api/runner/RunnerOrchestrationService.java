package ai.cloudforge.api.runner;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class RunnerOrchestrationService {

    private final RunnerRepository runnerRepository;
    private final RunnerAssignmentRepository assignmentRepository;
    private final EventPublisher eventPublisher;

    public RunnerOrchestrationService(
            RunnerRepository runnerRepository,
            RunnerAssignmentRepository assignmentRepository,
            EventPublisher eventPublisher) {
        this.runnerRepository = runnerRepository;
        this.assignmentRepository = assignmentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<RunnerResponse> getRunnersForProject(UUID projectId) {
        return runnerRepository.findByProjectId(projectId).stream()
                .map(RunnerResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RunnerResponse getRunnerById(UUID runnerId) {
        return runnerRepository.findById(runnerId)
                .map(RunnerResponse::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Runner not found"));
    }

    @Transactional
    public RunnerResponse registerRunner(
            UUID orgId,
            UUID userId,
            UUID projectId,
            String name,
            String runnerType,
            String runnerGroup,
            String token,
            String labels,
            String operatingSystem,
            Integer maxParallelJobs) {

        String tokenHash = hashToken(token);
        Runner runner = runnerRepository.save(new Runner(
                projectId, name, runnerType, runnerGroup, tokenHash, labels, operatingSystem, maxParallelJobs
        ));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "RUNNER_REGISTERED",
                name,
                "Runner agent " + name + " (" + runnerType + ") registered"
        ));

        return RunnerResponse.fromEntity(runner);
    }

    @Transactional
    public RunnerResponse heartbeat(UUID runnerId) {
        Runner runner = runnerRepository.findById(runnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Runner not found"));

        runner.updateHeartbeat();
        Runner saved = runnerRepository.save(runner);
        return RunnerResponse.fromEntity(saved);
    }

    @Transactional
    public RunnerResponse setRunnerStatus(UUID orgId, UUID userId, UUID runnerId, String status) {
        Runner runner = runnerRepository.findById(runnerId)
                .orElseThrow(() -> new ResourceNotFoundException("Runner not found"));

        runner.setStatus(status);
        Runner saved = runnerRepository.save(runner);

        String eventType = "DRAINING".equalsIgnoreCase(status) ? "RUNNER_DRAINING" : "RUNNER_UPDATED";
        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                eventType,
                saved.getName(),
                "Runner " + saved.getName() + " status set to " + status
        ));

        return RunnerResponse.fromEntity(saved);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return Integer.toHexString(rawToken.hashCode());
        }
    }

    public record RunnerResponse(
            UUID id,
            UUID projectId,
            String name,
            String runnerType,
            String runnerGroup,
            String status,
            String labels,
            String operatingSystem,
            Integer maxParallelJobs,
            Integer currentJobs
    ) {
        public static RunnerResponse fromEntity(Runner r) {
            return new RunnerResponse(
                    r.getId(), r.getProjectId(), r.getName(), r.getRunnerType(), r.getRunnerGroup(),
                    r.getStatus(), r.getLabels(), r.getOperatingSystem(), r.getMaxParallelJobs(), r.getCurrentJobs()
            );
        }
    }
}
