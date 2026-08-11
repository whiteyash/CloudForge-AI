-- V20__repository_synchronization_platform.sql: Enterprise Repository Synchronization Platform

-- 1. Imported Repositories Table
CREATE TABLE IF NOT EXISTS imported_repositories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    connection_id UUID REFERENCES git_provider_connections(id) ON DELETE SET NULL,
    external_repo_id VARCHAR(100) NOT NULL,
    name VARCHAR(150) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    provider_name VARCHAR(50) NOT NULL,
    clone_url VARCHAR(255) NOT NULL,
    default_branch VARCHAR(100) DEFAULT 'main',
    visibility VARCHAR(30) DEFAULT 'PRIVATE',
    language VARCHAR(50),
    size_in_bytes BIGINT DEFAULT 0,
    sync_status VARCHAR(30) DEFAULT 'SYNCHRONIZED',
    failure_reason TEXT,
    last_synced_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_successful_sync_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_failed_sync_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_project_external_repo_sync UNIQUE (project_id, external_repo_id)
);

-- 2. Repository Branches Table
CREATE TABLE IF NOT EXISTS repository_branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    commit_sha VARCHAR(40) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE,
    is_protected BOOLEAN DEFAULT FALSE,
    last_updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_repo_branch_sync UNIQUE (repository_id, name)
);

-- 3. Repository Sync Jobs Table
CREATE TABLE IF NOT EXISTS repository_sync_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    job_type VARCHAR(50) DEFAULT 'MANUAL',
    status VARCHAR(30) DEFAULT 'COMPLETED',
    started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    completed_at TIMESTAMPTZ,
    error_message TEXT
);
