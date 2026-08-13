-- V55__add_environment_to_teams.sql: Add environment column to teams table for environment scoping

ALTER TABLE teams
    ADD COLUMN IF NOT EXISTS environment VARCHAR(20) DEFAULT 'DEV';

UPDATE teams SET environment = 'DEV' WHERE environment IS NULL;

CREATE INDEX IF NOT EXISTS teams_org_env_idx ON teams(org_id, environment);
