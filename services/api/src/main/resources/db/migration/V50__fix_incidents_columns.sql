-- V50__fix_incidents_columns.sql: Add missing incidents columns for Hibernate JPA validation
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS organization_id UUID;
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS environment VARCHAR(30) DEFAULT 'DEV';
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS assignee_user_id UUID;
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS incident_source VARCHAR(50) DEFAULT 'MONITORING';
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS triggered_at TIMESTAMPTZ;
ALTER TABLE incidents ADD COLUMN IF NOT EXISTS acknowledged_at TIMESTAMPTZ;

CREATE INDEX IF NOT EXISTS idx_incidents_org ON incidents(organization_id);
