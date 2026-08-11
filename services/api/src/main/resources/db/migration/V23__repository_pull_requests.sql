-- V23__repository_pull_requests.sql: Enterprise Repository Pull Requests Table

CREATE TABLE IF NOT EXISTS repository_pull_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    external_pr_id VARCHAR(100) NOT NULL,
    number INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    state VARCHAR(30) DEFAULT 'OPEN',
    author_username VARCHAR(100) NOT NULL,
    author_avatar_url VARCHAR(255),
    source_branch VARCHAR(150) NOT NULL,
    target_branch VARCHAR(150) NOT NULL,
    is_draft BOOLEAN DEFAULT FALSE,
    web_url VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    closed_at TIMESTAMPTZ,
    merged_at TIMESTAMPTZ,
    CONSTRAINT uk_repo_pr_sync UNIQUE (repository_id, number)
);
