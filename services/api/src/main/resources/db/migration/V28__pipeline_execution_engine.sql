-- V28__pipeline_execution_engine.sql: Enterprise Pipeline Execution Engine

CREATE TABLE IF NOT EXISTS pipeline_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_id UUID NOT NULL REFERENCES pipelines(id) ON DELETE CASCADE,
    run_number INT NOT NULL,
    status VARCHAR(30) DEFAULT 'QUEUED',
    triggered_by VARCHAR(100) NOT NULL,
    correlation_id UUID NOT NULL DEFAULT gen_random_uuid(),
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS pipeline_stages (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_run_id UUID NOT NULL REFERENCES pipeline_runs(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    stage_order INT NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING',
    requires_approval BOOLEAN DEFAULT FALSE,
    is_approved BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS pipeline_jobs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    stage_id UUID NOT NULL REFERENCES pipeline_stages(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING'
);
