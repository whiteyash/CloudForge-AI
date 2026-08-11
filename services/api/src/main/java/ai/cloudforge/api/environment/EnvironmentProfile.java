package ai.cloudforge.api.environment;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "environments")
public class EnvironmentProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "environment_type", nullable = false, length = 50)
    private String environmentType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 30)
    private String status = "ACTIVE";

    @Column(name = "is_protected")
    private boolean isProtected = false;

    @Column(name = "is_maintenance_mode")
    private boolean isMaintenanceMode = false;

    @Column(name = "is_frozen")
    private boolean isFrozen = false;

    @Column(name = "health_status", length = 30)
    private String healthStatus = "HEALTHY";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected EnvironmentProfile() {
    }

    public EnvironmentProfile(UUID projectId, String name, String environmentType, String description, boolean isProtected) {
        this.projectId = projectId;
        this.name = name;
        this.environmentType = environmentType;
        this.description = description;
        this.isProtected = isProtected;
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public String getEnvironmentType() {
        return environmentType;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public boolean isProtected() {
        return isProtected;
    }

    public boolean isMaintenanceMode() {
        return isMaintenanceMode;
    }

    public boolean isFrozen() {
        return isFrozen;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMaintenanceMode(boolean maintenanceMode) {
        this.isMaintenanceMode = maintenanceMode;
        if (maintenanceMode) {
            this.status = "MAINTENANCE";
        } else if ("MAINTENANCE".equals(this.status)) {
            this.status = "ACTIVE";
        }
    }

    public void setFrozen(boolean frozen) {
        this.isFrozen = frozen;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }
}
