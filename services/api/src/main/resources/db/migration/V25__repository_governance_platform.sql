-- V25__repository_governance_platform.sql: Enterprise Repository Governance Policies

CREATE TABLE IF NOT EXISTS repository_governance_policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    branch_protection_enabled BOOLEAN DEFAULT FALSE,
    required_reviews_count INT DEFAULT 1,
    signed_commits_required BOOLEAN DEFAULT FALSE,
    secret_scanning_enabled BOOLEAN DEFAULT FALSE,
    dependabot_enabled BOOLEAN DEFAULT FALSE,
    code_scanning_enabled BOOLEAN DEFAULT FALSE,
    risk_score INT DEFAULT 20,
    compliance_score INT DEFAULT 80,
    violation_count INT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_repo_governance UNIQUE (repository_id)
);
