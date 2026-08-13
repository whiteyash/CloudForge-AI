-- V52__add_environment_and_indexes_to_audit_logs.sql: Add environment column & indexes for Activity Timeline querying

ALTER TABLE audit_logs
    ADD COLUMN IF NOT EXISTS environment VARCHAR(20) NOT DEFAULT 'DEV';

UPDATE audit_logs SET environment = 'DEV' WHERE environment IS NULL;

ALTER TABLE audit_logs ALTER COLUMN environment SET DEFAULT 'DEV';

CREATE INDEX IF NOT EXISTS audit_logs_org_env_created_idx
    ON audit_logs(org_id, environment, created_at DESC);

CREATE INDEX IF NOT EXISTS audit_logs_org_created_idx
    ON audit_logs(org_id, created_at DESC);
