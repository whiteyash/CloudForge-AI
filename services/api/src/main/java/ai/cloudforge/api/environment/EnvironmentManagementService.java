package ai.cloudforge.api.environment;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class EnvironmentManagementService {

    private final EnvironmentProfileRepository profileRepository;
    private final EnvironmentVariableRepository variableRepository;
    private final EnvironmentTargetRepository targetRepository;
    private final EventPublisher eventPublisher;

    public EnvironmentManagementService(
            EnvironmentProfileRepository profileRepository,
            EnvironmentVariableRepository variableRepository,
            EnvironmentTargetRepository targetRepository,
            EventPublisher eventPublisher) {
        this.profileRepository = profileRepository;
        this.variableRepository = variableRepository;
        this.targetRepository = targetRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<EnvironmentResponse> getEnvironmentsForProject(UUID projectId) {
        return profileRepository.findByProjectId(projectId).stream()
                .map(EnvironmentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public EnvironmentDetailResponse getEnvironmentById(UUID environmentId) {
        EnvironmentProfile profile = profileRepository.findById(environmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Environment profile not found"));

        List<VariableResponse> vars = variableRepository.findByEnvironmentId(environmentId).stream()
                .map(VariableResponse::fromEntity)
                .toList();

        List<TargetResponse> targets = targetRepository.findByEnvironmentId(environmentId).stream()
                .map(TargetResponse::fromEntity)
                .toList();

        return new EnvironmentDetailResponse(EnvironmentResponse.fromEntity(profile), vars, targets);
    }

    @Transactional
    public EnvironmentResponse createEnvironment(
            UUID orgId,
            UUID userId,
            UUID projectId,
            String name,
            String environmentType,
            String description,
            boolean isProtected) {

        EnvironmentProfile profile = profileRepository.save(new EnvironmentProfile(
                projectId, name, environmentType, description, isProtected
        ));

        // Create default target binding for logical environment
        targetRepository.save(new EnvironmentTarget(
                profile.getId(), name.toLowerCase() + "-target", "KUBERNETES_NAMESPACE", "k8s://" + name.toLowerCase()
        ));

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "ENVIRONMENT_CREATED",
                name,
                "Environment " + name + " (" + environmentType + ") created"
        ));

        return EnvironmentResponse.fromEntity(profile);
    }

    @Transactional
    public EnvironmentResponse setMaintenanceMode(UUID orgId, UUID userId, UUID environmentId, boolean enabled) {
        EnvironmentProfile profile = profileRepository.findById(environmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Environment profile not found"));

        profile.setMaintenanceMode(enabled);
        EnvironmentProfile saved = profileRepository.save(profile);

        String eventType = enabled ? "ENVIRONMENT_MAINTENANCE_ENABLED" : "ENVIRONMENT_MAINTENANCE_DISABLED";
        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                eventType,
                saved.getName(),
                "Environment " + saved.getName() + " maintenance mode set to " + enabled
        ));

        return EnvironmentResponse.fromEntity(saved);
    }

    @Transactional
    public EnvironmentResponse setFrozenStatus(UUID orgId, UUID userId, UUID environmentId, boolean frozen) {
        EnvironmentProfile profile = profileRepository.findById(environmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Environment profile not found"));

        profile.setFrozen(frozen);
        EnvironmentProfile saved = profileRepository.save(profile);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "ENVIRONMENT_UPDATED",
                saved.getName(),
                "Environment " + saved.getName() + " freeze window set to " + frozen
        ));

        return EnvironmentResponse.fromEntity(saved);
    }

    @Transactional
    public EnvironmentResponse setStatus(UUID orgId, UUID userId, UUID environmentId, String status) {
        EnvironmentProfile profile = profileRepository.findById(environmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Environment profile not found"));

        profile.setStatus(status);
        EnvironmentProfile saved = profileRepository.save(profile);

        String eventType = "ACTIVE".equalsIgnoreCase(status) ? "ENVIRONMENT_ACTIVATED" : "ENVIRONMENT_DEACTIVATED";
        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                eventType,
                saved.getName(),
                "Environment " + saved.getName() + " status set to " + status
        ));

        return EnvironmentResponse.fromEntity(saved);
    }

    public record EnvironmentResponse(
            UUID id,
            UUID projectId,
            String name,
            String environmentType,
            String description,
            String status,
            boolean isProtected,
            boolean isMaintenanceMode,
            boolean isFrozen,
            String healthStatus
    ) {
        public static EnvironmentResponse fromEntity(EnvironmentProfile e) {
            return new EnvironmentResponse(
                    e.getId(), e.getProjectId(), e.getName(), e.getEnvironmentType(),
                    e.getDescription(), e.getStatus(), e.isProtected(), e.isMaintenanceMode(),
                    e.isFrozen(), e.getHealthStatus()
            );
        }
    }

    public record VariableResponse(
            UUID id,
            String keyName,
            String value,
            boolean isSecret
    ) {
        public static VariableResponse fromEntity(EnvironmentVariable v) {
            return new VariableResponse(v.getId(), v.getKeyName(), v.getValue(), v.isSecret());
        }
    }

    public record TargetResponse(
            UUID id,
            String targetName,
            String targetType,
            String connectionEndpoint
    ) {
        public static TargetResponse fromEntity(EnvironmentTarget t) {
            return new TargetResponse(t.getId(), t.getTargetName(), t.getTargetType(), t.getConnectionEndpoint());
        }
    }

    public record EnvironmentDetailResponse(
            EnvironmentResponse environment,
            List<VariableResponse> variables,
            List<TargetResponse> targets
    ) {}
}
