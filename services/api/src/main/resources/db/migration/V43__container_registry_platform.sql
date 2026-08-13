-- V43__container_registry_platform.sql: Enterprise Container Registry & Native Image Building Platform

CREATE TABLE IF NOT EXISTS container_registries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    registry_type VARCHAR(50) NOT NULL,
    registry_url VARCHAR(512) NOT NULL,
    auth_type VARCHAR(50) NOT NULL,
    encrypted_credentials TEXT,
    status VARCHAR(30) NOT NULL DEFAULT 'CONNECTED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_container_registries_project ON container_registries(project_id);
CREATE INDEX IF NOT EXISTS idx_container_registries_org ON container_registries(organization_id);

CREATE TABLE IF NOT EXISTS container_repositories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    registry_id UUID NOT NULL REFERENCES container_registries(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    repository_name VARCHAR(255) NOT NULL,
    image_count INT NOT NULL DEFAULT 0,
    pull_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_container_repos_registry ON container_repositories(registry_id);
CREATE INDEX IF NOT EXISTS idx_container_repos_project ON container_repositories(project_id);

CREATE TABLE IF NOT EXISTS container_image_tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES container_repositories(id) ON DELETE CASCADE,
    tag_name VARCHAR(128) NOT NULL,
    digest_sha256 VARCHAR(128) NOT NULL,
    size_bytes BIGINT NOT NULL DEFAULT 0,
    architecture VARCHAR(50) NOT NULL DEFAULT 'linux/amd64',
    is_immutable BOOLEAN NOT NULL DEFAULT false,
    pushed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_container_tags_repo ON container_image_tags(repository_id);

CREATE TABLE IF NOT EXISTS native_image_builds (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    registry_id UUID NOT NULL REFERENCES container_registries(id) ON DELETE CASCADE,
    repository_name VARCHAR(255) NOT NULL,
    tag_name VARCHAR(128) NOT NULL,
    dockerfile_path VARCHAR(255) NOT NULL DEFAULT 'Dockerfile',
    status VARCHAR(30) NOT NULL DEFAULT 'QUEUED',
    log_output TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_native_image_builds_project ON native_image_builds(project_id);
CREATE INDEX IF NOT EXISTS idx_native_image_builds_registry ON native_image_builds(registry_id);

-- Register permissions in RBAC system matrix
INSERT INTO permissions (id, code, module, description)
VALUES 
    (gen_random_uuid(), 'REGISTRY_VIEW', 'REGISTRY', 'Allows viewing connected container registries and image tags'),
    (gen_random_uuid(), 'REGISTRY_MANAGE', 'REGISTRY', 'Allows connecting and disconnecting container registries'),
    (gen_random_uuid(), 'IMAGE_BUILD', 'REGISTRY', 'Allows triggering container image builds'),
    (gen_random_uuid(), 'IMAGE_DELETE', 'REGISTRY', 'Allows deleting non-immutable container image tags')
ON CONFLICT (code) DO NOTHING;


-- Map permissions to Roles
INSERT INTO role_permissions (role, permission_code) VALUES
('OWNER', 'REGISTRY_VIEW'),
('OWNER', 'REGISTRY_MANAGE'),
('OWNER', 'IMAGE_BUILD'),
('OWNER', 'IMAGE_DELETE'),
('ADMIN', 'REGISTRY_VIEW'),
('ADMIN', 'REGISTRY_MANAGE'),
('ADMIN', 'IMAGE_BUILD'),
('ADMIN', 'IMAGE_DELETE'),
('DEVOPS', 'REGISTRY_VIEW'),
('DEVOPS', 'REGISTRY_MANAGE'),
('DEVOPS', 'IMAGE_BUILD'),
('DEVOPS', 'IMAGE_DELETE'),
('DEVELOPER', 'REGISTRY_VIEW'),
('DEVELOPER', 'IMAGE_BUILD'),
('SECURITY', 'REGISTRY_VIEW'),
('VIEWER', 'REGISTRY_VIEW')
ON CONFLICT (role, permission_code) DO NOTHING;
