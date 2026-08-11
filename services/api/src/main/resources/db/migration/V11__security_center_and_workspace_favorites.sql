-- V11__security_center_and_workspace_favorites.sql: Security Center & Workspace Favorites

-- 1. Extend Users table for password age and MFA readiness
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS password_changed_at TIMESTAMPTZ DEFAULT now(),
    ADD COLUMN IF NOT EXISTS mfa_enabled BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS mfa_secret VARCHAR(255);

-- 2. Extend Active Sessions for trusted device tracking
ALTER TABLE active_sessions
    ADD COLUMN IF NOT EXISTS is_trusted BOOLEAN DEFAULT FALSE;
