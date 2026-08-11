-- V31__artifact_repository_platform.sql: Enterprise Artifact Repository Platform

CREATE TABLE IF NOT EXISTS artifacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    pipeline_run_id UUID REFERENCES pipeline_runs(id) ON DELETE SET NULL,
    job_id UUID REFERENCES job_executions(id) ON DELETE SET NULL,
    name VARCHAR(150) NOT NULL,
    artifact_type VARCHAR(50) NOT NULL,
    version VARCHAR(50) DEFAULT '1.0.0',
    sha256_checksum VARCHAR(64) NOT NULL,
    size_bytes BIGINT NOT NULL,
    mime_type VARCHAR(100) DEFAULT 'application/octet-stream',
    storage_provider VARCHAR(50) DEFAULT 'LOCAL',
    storage_key VARCHAR(255) NOT NULL,
    retention_status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS artifact_downloads (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    artifact_id UUID NOT NULL REFERENCES artifacts(id) ON DELETE CASCADE,
    downloaded_by VARCHAR(100) NOT NULL,
    downloaded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
