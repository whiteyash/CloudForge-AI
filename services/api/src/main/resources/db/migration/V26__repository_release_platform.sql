-- V26__repository_release_platform.sql: Enterprise Repository Release Platform

CREATE TABLE IF NOT EXISTS repository_releases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    repository_id UUID NOT NULL REFERENCES imported_repositories(id) ON DELETE CASCADE,
    external_release_id VARCHAR(100) NOT NULL,
    tag_name VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    body TEXT,
    author_username VARCHAR(100) NOT NULL,
    author_avatar_url VARCHAR(255),
    is_draft BOOLEAN DEFAULT FALSE,
    is_prerelease BOOLEAN DEFAULT FALSE,
    published_at TIMESTAMPTZ,
    web_url VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_repo_release_tag UNIQUE (repository_id, tag_name)
);
