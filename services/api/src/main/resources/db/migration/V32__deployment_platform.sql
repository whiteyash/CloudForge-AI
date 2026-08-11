-- V32__deployment_platform.sql: Enterprise Deployment Platform

CREATE TABLE IF NOT EXISTS deployments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    pipeline_run_id UUID REFERENCES pipeline_runs(id) ON DELETE SET NULL,
    artifact_id UUID REFERENCES artifacts(id) ON DELETE SET NULL,
    target_name VARCHAR(100) NOT NULL,
    strategy VARCHAR(50) DEFAULT 'ROLLING',
    status VARCHAR(30) DEFAULT 'QUEUED',
    idempotency_key VARCHAR(100) UNIQUE NOT NULL,
    requested_by VARCHAR(100) NOT NULL,
    approved_by VARCHAR(100),
    failure_reason TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS deployment_rollbacks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    deployment_id UUID NOT NULL REFERENCES deployments(id) ON DELETE CASCADE,
    target_deployment_id UUID NOT NULL REFERENCES deployments(id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    initiated_by VARCHAR(100) NOT NULL,
    status VARCHAR(30) DEFAULT 'COMPLETED',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
