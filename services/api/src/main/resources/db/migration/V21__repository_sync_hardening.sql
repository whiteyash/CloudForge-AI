-- V21__repository_sync_hardening.sql: Repository Sync Metadata & Commit Hardening

ALTER TABLE imported_repositories
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS is_private BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS is_fork BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS ssh_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS web_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS stargazers_count INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS forks_count INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS open_issues_count INT DEFAULT 0,
    ADD COLUMN IF NOT EXISTS pushed_at TIMESTAMPTZ;

-- Commit Metadata Table
CREATE TABLE IF NOT EXISTS repository_commits (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    commit_sha VARCHAR(40) NOT NULL,
    short_sha VARCHAR(10) NOT NULL,
    message TEXT NOT NULL,
    author_name VARCHAR(100),
    author_email VARCHAR(100),
    committer_name VARCHAR(100),
    committer_email VARCHAR(100),
    committed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    web_url VARCHAR(255),
    CONSTRAINT uk_repo_commit_sync UNIQUE (repository_id, commit_sha)
);

-- Contributors Table
CREATE TABLE IF NOT EXISTS repository_contributors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    username VARCHAR(100) NOT NULL,
    display_name VARCHAR(150),
    avatar_url VARCHAR(255),
    contribution_count INT DEFAULT 1,
    CONSTRAINT uk_repo_contributor_sync UNIQUE (repository_id, username)
);
