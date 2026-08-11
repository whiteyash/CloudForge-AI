-- V6__membership_and_invitations.sql: Membership Status & Advanced Invitations

-- 1. Extend Org Invitations table
ALTER TABLE org_invitations
    ADD COLUMN IF NOT EXISTS attempts_count INT DEFAULT 1,
    ADD COLUMN IF NOT EXISTS resent_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS cancelled_at TIMESTAMPTZ;

-- 2. Extend Memberships table for status & tracking
ALTER TABLE memberships
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS last_active_at TIMESTAMPTZ;

-- 3. Membership Role History Table
CREATE TABLE IF NOT EXISTS membership_role_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    old_role VARCHAR(40),
    new_role VARCHAR(40) NOT NULL,
    changed_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS membership_role_history_org_idx ON membership_role_history(org_id);
