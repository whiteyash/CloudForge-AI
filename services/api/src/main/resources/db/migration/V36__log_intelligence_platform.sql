-- V36__log_intelligence_platform.sql: Enterprise Log Intelligence Platform

CREATE TABLE IF NOT EXISTS log_entries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    source_type VARCHAR(50) NOT NULL,
    severity VARCHAR(30) DEFAULT 'ERROR',
    log_message TEXT NOT NULL,
    stack_trace TEXT,
    timestamp TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS log_clusters (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    cluster_name VARCHAR(150) NOT NULL,
    severity VARCHAR(30) DEFAULT 'ERROR',
    occurrence_count INT DEFAULT 1,
    affected_services VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS exception_fingerprints (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    fingerprint_hash VARCHAR(64) NOT NULL,
    exception_class VARCHAR(255) NOT NULL,
    failed_method VARCHAR(150) NOT NULL,
    failed_file VARCHAR(255) NOT NULL,
    line_number INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS log_analysis_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    log_entry_id UUID REFERENCES log_entries(id) ON DELETE CASCADE,
    summary TEXT NOT NULL,
    root_cause TEXT NOT NULL,
    confidence_score INT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
