-- V24__repository_insights_platform.sql: Enterprise Repository Insights Snapshots

CREATE TABLE IF NOT EXISTS repository_insights_snapshots (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    snapshot_date DATE NOT NULL,
    total_commits INT DEFAULT 0,
    total_branches INT DEFAULT 0,
    total_contributors INT DEFAULT 0,
    open_prs INT DEFAULT 0,
    merged_prs INT DEFAULT 0,
    closed_prs INT DEFAULT 0,
    health_score INT DEFAULT 100,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_repo_snapshot_date UNIQUE (repository_id, snapshot_date)
);
