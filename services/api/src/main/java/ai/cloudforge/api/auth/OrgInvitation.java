package ai.cloudforge.api.auth;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_invitations")
public class OrgInvitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invited_by", nullable = false)
    private AppUser invitedBy;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING | ACCEPTED | REJECTED | EXPIRED | CANCELLED

    @Column(name = "attempts_count")
    private int attemptsCount = 1;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "resent_at")
    private Instant resentAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected OrgInvitation() {
    }

    public OrgInvitation(Organization organization, String email, Role role, AppUser invitedBy, String token, Instant expiresAt) {
        this.organization = organization;
        this.email = email;
        this.role = role;
        this.invitedBy = invitedBy;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public AppUser getInvitedBy() {
        return invitedBy;
    }

    public String getToken() {
        return token;
    }

    public String getStatus() {
        return status;
    }

    public int getAttemptsCount() {
        return attemptsCount;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getResentAt() {
        return resentAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void accept() {
        this.status = "ACCEPTED";
    }

    public void reject() {
        this.status = "REJECTED";
    }

    public void cancel() {
        this.status = "CANCELLED";
        this.cancelledAt = Instant.now();
    }

    public void resend(Instant newExpiresAt) {
        this.attemptsCount++;
        this.resentAt = Instant.now();
        this.expiresAt = newExpiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
