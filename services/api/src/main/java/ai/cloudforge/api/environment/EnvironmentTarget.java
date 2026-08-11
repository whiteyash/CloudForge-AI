package ai.cloudforge.api.environment;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "environment_targets")
public class EnvironmentTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "environment_id", nullable = false)
    private UUID environmentId;

    @Column(name = "target_name", nullable = false, length = 100)
    private String targetName;

    @Column(name = "target_type", nullable = false, length = 50)
    private String targetType;

    @Column(name = "connection_endpoint", nullable = false, length = 255)
    private String connectionEndpoint;

    protected EnvironmentTarget() {
    }

    public EnvironmentTarget(UUID environmentId, String targetName, String targetType, String connectionEndpoint) {
        this.environmentId = environmentId;
        this.targetName = targetName;
        this.targetType = targetType;
        this.connectionEndpoint = connectionEndpoint;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEnvironmentId() {
        return environmentId;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getTargetType() {
        return targetType;
    }

    public String getConnectionEndpoint() {
        return connectionEndpoint;
    }
}
