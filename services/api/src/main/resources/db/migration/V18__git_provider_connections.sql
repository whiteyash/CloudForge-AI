-- V18__git_provider_connections.sql: OAuth 2.0 Git Provider Connections Schema

CREATE TABLE IF NOT EXISTS git_provider_connections (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    provider_name VARCHAR(50) NOT NULL, -- GITHUB, GITLAB, BITBUCKET
    account_name VARCHAR(100) NOT NULL,
    encrypted_access_token TEXT NOT NULL,
    encrypted_refresh_token TEXT,
    token_expires_at TIMESTAMPTZ,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_org_provider_account UNIQUE (org_id, provider_name, account_name)
);

CREATE INDEX IF NOT EXISTS git_provider_connections_org_idx ON git_provider_connections(org_id);
