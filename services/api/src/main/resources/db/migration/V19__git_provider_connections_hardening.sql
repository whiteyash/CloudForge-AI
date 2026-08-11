-- V19__git_provider_connections_hardening.sql: Git Provider Connections Hardening

ALTER TABLE git_provider_connections
    ADD COLUMN IF NOT EXISTS granted_scopes VARCHAR(255),
    ADD COLUMN IF NOT EXISTS health_status VARCHAR(30) DEFAULT 'CONNECTED',
    ADD COLUMN IF NOT EXISTS failure_reason TEXT,
    ADD COLUMN IF NOT EXISTS rate_limit_remaining INT DEFAULT 5000,
    ADD COLUMN IF NOT EXISTS rate_limit_reset_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS last_synced_at TIMESTAMPTZ;
