package ai.cloudforge.api.git;

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
@Table(name = "git_provider_connections")
public class GitProviderConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "org_id", nullable = false)
    private UUID orgId;

    @Column(name = "provider_name", nullable = false, length = 50)
    private String providerName;

    @Column(name = "account_name", nullable = false, length = 100)
    private String accountName;

    @Column(name = "encrypted_access_token", nullable = false, columnDefinition = "TEXT")
    private String encryptedAccessToken;

    @Column(name = "encrypted_refresh_token", columnDefinition = "TEXT")
    private String encryptedRefreshToken;

    @Column(name = "token_expires_at")
    private Instant tokenExpiresAt;

    @Column(length = 30)
    private String status = "ACTIVE";

    @Column(name = "granted_scopes", length = 255)
    private String grantedScopes = "repo, read:org";

    @Column(name = "health_status", length = 30)
    private String healthStatus = "CONNECTED";

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    @Column(name = "rate_limit_remaining")
    private Integer rateLimitRemaining = 5000;

    @Column(name = "rate_limit_reset_at")
    private Instant rateLimitResetAt;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected GitProviderConnection() {
    }

    public GitProviderConnection(UUID orgId, String providerName, String accountName, String encryptedAccessToken, String encryptedRefreshToken, String grantedScopes) {
        this.orgId = orgId;
        this.providerName = providerName;
        this.accountName = accountName;
        this.encryptedAccessToken = encryptedAccessToken;
        this.encryptedRefreshToken = encryptedRefreshToken;
        this.grantedScopes = grantedScopes != null ? grantedScopes : "repo, read:org";
    }

    @PrePersist
    @PreUpdate
    void onSave() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
        if (lastSyncedAt == null) {
            lastSyncedAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrgId() {
        return orgId;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getEncryptedAccessToken() {
        return encryptedAccessToken;
    }

    public String getEncryptedRefreshToken() {
        return encryptedRefreshToken;
    }

    public Instant getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public String getStatus() {
        return status;
    }

    public String getGrantedScopes() {
        return grantedScopes;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public Integer getRateLimitRemaining() {
        return rateLimitRemaining;
    }

    public Instant getRateLimitResetAt() {
        return rateLimitResetAt;
    }

    public Instant getLastSyncedAt() {
        return lastSyncedAt;
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

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public void setLastSyncedAt(Instant lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }
}
