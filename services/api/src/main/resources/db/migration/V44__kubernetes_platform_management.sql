-- Flyway Migration V44: Kubernetes Multi-Cluster Platform Management & GitOps Sync Engine

CREATE TABLE IF NOT EXISTS k8s_clusters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    api_server_url VARCHAR(255) NOT NULL,
    encrypted_kubeconfig TEXT NULL,
    environment VARCHAR(50) NOT NULL DEFAULT 'PRODUCTION',
    status VARCHAR(50) NOT NULL DEFAULT 'CONNECTED',
    running_pods INT NOT NULL DEFAULT 0,
    total_nodes INT NOT NULL DEFAULT 0,
    cpu_utilization_pct DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    memory_utilization_pct DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    last_synced_at TIMESTAMP WITH TIME ZONE NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_k8s_clusters_project_name UNIQUE (project_id, name)
);

CREATE TABLE IF NOT EXISTS helm_releases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cluster_id UUID NOT NULL REFERENCES k8s_clusters(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    release_name VARCHAR(100) NOT NULL,
    namespace VARCHAR(100) NOT NULL DEFAULT 'default',
    chart_name VARCHAR(100) NOT NULL,
    chart_version VARCHAR(50) NOT NULL,
    values_yaml TEXT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'DEPLOYED',
    deployed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gitops_sync_configs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cluster_id UUID NOT NULL REFERENCES k8s_clusters(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    repo_url VARCHAR(255) NOT NULL,
    target_revision VARCHAR(100) NOT NULL DEFAULT 'main',
    path VARCHAR(255) NOT NULL DEFAULT '/',
    sync_status VARCHAR(50) NOT NULL DEFAULT 'SYNCED',
    last_synced_at TIMESTAMP WITH TIME ZONE NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_k8s_clusters_project ON k8s_clusters(project_id);
CREATE INDEX IF NOT EXISTS idx_k8s_clusters_org ON k8s_clusters(organization_id);
CREATE INDEX IF NOT EXISTS idx_helm_releases_cluster ON helm_releases(cluster_id);
CREATE INDEX IF NOT EXISTS idx_helm_releases_project ON helm_releases(project_id);
CREATE INDEX IF NOT EXISTS idx_gitops_sync_cluster ON gitops_sync_configs(cluster_id);
CREATE INDEX IF NOT EXISTS idx_gitops_sync_project ON gitops_sync_configs(project_id);
