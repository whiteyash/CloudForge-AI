-- V7__activity_timeline_and_workspace_history.sql: Activity Timeline & Workspace History

-- 1. Extend Audit Logs table with metadata payload
ALTER TABLE audit_logs
    ADD COLUMN IF NOT EXISTS actor_email VARCHAR(255),
    ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45) DEFAULT '127.0.0.1',
    ADD COLUMN IF NOT EXISTS metadata_json TEXT;

-- 2. Workspace Switching History Table
CREATE TABLE IF NOT EXISTS workspace_switch_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    from_org_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
    to_org_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    switched_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS workspace_switch_history_user_idx ON workspace_switch_history(user_id);
