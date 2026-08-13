-- V53__enhance_team_schema.sql: Enhance teams and team_memberships for production enterprise features

ALTER TABLE teams 
    ADD COLUMN IF NOT EXISTS slug VARCHAR(140),
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT now(),
    ADD COLUMN IF NOT EXISTS archived_at TIMESTAMPTZ;

ALTER TABLE team_memberships 
    ADD COLUMN IF NOT EXISTS role VARCHAR(40) DEFAULT 'MEMBER';

CREATE INDEX IF NOT EXISTS teams_org_id_idx ON teams(org_id);
CREATE INDEX IF NOT EXISTS team_memberships_team_id_idx ON team_memberships(team_id);
CREATE INDEX IF NOT EXISTS team_memberships_user_id_idx ON team_memberships(user_id);
