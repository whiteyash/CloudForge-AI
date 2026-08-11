package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_subscriptions")
public class OrgSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false, unique = true)
    private Organization organization;

    @Column(name = "plan_tier", nullable = false, length = 30)
    private String planTier = "FREE"; // FREE | PRO | ENTERPRISE

    @Column(name = "seat_limit", nullable = false)
    private int seatLimit = 5;

    @Column(name = "storage_limit_gb", nullable = false)
    private int storageLimitGb = 10;

    @Column(name = "api_rate_limit_per_min", nullable = false)
    private int apiRateLimitPerMin = 1000;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "current_period_start", nullable = false)
    private Instant currentPeriodStart;

    @Column(name = "current_period_end", nullable = false)
    private Instant currentPeriodEnd;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected OrgSubscription() {
    }

    public OrgSubscription(Organization organization, String planTier, int seatLimit, int storageLimitGb, int apiRateLimitPerMin) {
        this.organization = organization;
        this.planTier = planTier;
        this.seatLimit = seatLimit;
        this.storageLimitGb = storageLimitGb;
        this.apiRateLimitPerMin = apiRateLimitPerMin;
        this.currentPeriodStart = Instant.now();
        this.currentPeriodEnd = Instant.now().plusSeconds(30 * 24 * 3600);
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (currentPeriodStart == null) {
            currentPeriodStart = Instant.now();
        }
        if (currentPeriodEnd == null) {
            currentPeriodEnd = Instant.now().plusSeconds(30 * 24 * 3600);
        }
    }

    public UUID getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getPlanTier() {
        return planTier;
    }

    public int getSeatLimit() {
        return seatLimit;
    }

    public int getStorageLimitGb() {
        return storageLimitGb;
    }

    public int getApiRateLimitPerMin() {
        return apiRateLimitPerMin;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCurrentPeriodStart() {
        return currentPeriodStart;
    }

    public Instant getCurrentPeriodEnd() {
        return currentPeriodEnd;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
