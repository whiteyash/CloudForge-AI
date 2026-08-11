-- V30__job_execution_platform.sql: Enterprise Job Execution & Live Log Streaming Platform

CREATE TABLE IF NOT EXISTS job_executions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    pipeline_run_id UUID NOT NULL REFERENCES pipeline_runs(id) ON DELETE CASCADE,
    job_name VARCHAR(150) NOT NULL,
    runner_id UUID REFERENCES runners(id) ON DELETE SET NULL,
    status VARCHAR(30) DEFAULT 'QUEUED',
    exit_code INT,
    retry_count INT DEFAULT 0,
    duration_ms BIGINT DEFAULT 0,
    failure_reason TEXT,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS job_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_execution_id UUID NOT NULL REFERENCES job_executions(id) ON DELETE CASCADE,
    sequence_number INT NOT NULL,
    log_line TEXT NOT NULL,
    stream_type VARCHAR(20) DEFAULT 'STDOUT',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
