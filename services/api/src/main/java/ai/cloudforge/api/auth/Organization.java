package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(length = 50)
    private String timezone = "UTC";

    @Column(length = 20)
    private String status = "ACTIVE"; // ACTIVE | ARCHIVED | DELETED

    @Column(name = "primary_color", length = 10)
    private String primaryColor = "#3DD9C4";

    @Column(name = "member_approval_policy", length = 30)
    private String memberApprovalPolicy = "ANY_ADMIN";

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Organization() {
    }

    public Organization(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    @PrePersist
    void createTimestamp() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public String getDescription() {
        return description;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getStatus() {
        return status;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getMemberApprovalPolicy() {
        return memberApprovalPolicy;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void update(String name, String description, String websiteUrl, String timezone, String primaryColor) {
        if (name != null && !name.isBlank()) this.name = name.trim();
        this.description = description;
        this.websiteUrl = websiteUrl;
        if (timezone != null && !timezone.isBlank()) this.timezone = timezone;
        if (primaryColor != null && !primaryColor.isBlank()) this.primaryColor = primaryColor;
    }

    public void archive() {
        this.status = "ARCHIVED";
    }

    public void restore() {
        this.status = "ACTIVE";
        this.deletedAt = null;
    }

    public void softDelete() {
        this.status = "DELETED";
        this.deletedAt = Instant.now();
    }
}
