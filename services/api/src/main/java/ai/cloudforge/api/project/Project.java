package ai.cloudforge.api.project;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import ai.cloudforge.api.auth.Organization;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "repo_url", length = 255)
    private String repoUrl;

    @Column(name = "k8s_namespace", length = 120)
    private String k8sNamespace;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected Project() {
    }

    public Project(Organization organization, String name, String repoUrl, String k8sNamespace) {
        this.organization = organization;
        this.name = name;
        this.repoUrl = repoUrl;
        this.k8sNamespace = k8sNamespace;
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

    public String getName() {
        return name;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public String getK8sNamespace() {
        return k8sNamespace;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void update(String name, String repoUrl, String k8sNamespace) {
        this.name = name;
        this.repoUrl = repoUrl;
        this.k8sNamespace = k8sNamespace;
    }
}
