-- V29__runner_platform.sql: Enterprise Runner Orchestration Platform

CREATE TABLE IF NOT EXISTS runners (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    runner_type VARCHAR(50) NOT NULL,
    runner_group VARCHAR(100) DEFAULT 'default',
    token_hash VARCHAR(64) NOT NULL,
    status VARCHAR(30) DEFAULT 'ONLINE',
    labels VARCHAR(255) DEFAULT 'ubuntu-latest',
    operating_system VARCHAR(50) DEFAULT 'linux',
    max_parallel_jobs INT DEFAULT 2,
    current_jobs INT DEFAULT 0,
    last_heartbeat TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS runner_assignments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    runner_id UUID NOT NULL REFERENCES runners(id) ON DELETE CASCADE,
    job_id UUID NOT NULL REFERENCES pipeline_jobs(id) ON DELETE CASCADE,
    status VARCHAR(30) DEFAULT 'ASSIGNED',
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
