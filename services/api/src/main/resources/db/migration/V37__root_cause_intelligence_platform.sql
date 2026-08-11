-- V37__root_cause_intelligence_platform.sql: Enterprise Root Cause Intelligence Platform

CREATE TABLE IF NOT EXISTS root_cause_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    incident_id UUID REFERENCES incidents(id) ON DELETE CASCADE,
    summary TEXT NOT NULL,
    root_cause TEXT NOT NULL,
    confidence_score INT NOT NULL,
    risk_rating VARCHAR(30) DEFAULT 'HIGH',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS causal_graphs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    root_cause_report_id UUID REFERENCES root_cause_reports(id) ON DELETE CASCADE,
    graph_json TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS dependency_links (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    source_component VARCHAR(150) NOT NULL,
    target_component VARCHAR(150) NOT NULL,
    link_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS evidence_records (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    root_cause_report_id UUID REFERENCES root_cause_reports(id) ON DELETE CASCADE,
    source_system VARCHAR(50) NOT NULL,
    evidence_text TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
