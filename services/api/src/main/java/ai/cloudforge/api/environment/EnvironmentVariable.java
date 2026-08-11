package ai.cloudforge.api.environment;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "environment_variables")
public class EnvironmentVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "environment_id", nullable = false)
    private UUID environmentId;

    @Column(name = "key_name", nullable = false, length = 100)
    private String keyName;

    @Column(nullable = false, length = 255)
    private String value;

    @Column(name = "is_secret")
    private boolean isSecret = false;

    protected EnvironmentVariable() {
    }

    public EnvironmentVariable(UUID environmentId, String keyName, String value, boolean isSecret) {
        this.environmentId = environmentId;
        this.keyName = keyName;
        this.value = value;
        this.isSecret = isSecret;
    }

    public UUID getId() {
        return id;
    }

    public UUID getEnvironmentId() {
        return environmentId;
    }

    public String getKeyName() {
        return keyName;
    }

    public String getValue() {
        return value;
    }

    public boolean isSecret() {
        return isSecret;
    }
}
