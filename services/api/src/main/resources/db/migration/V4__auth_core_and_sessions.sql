-- V4__auth_core_and_sessions.sql: Auth Core, Tokens, Sessions, and Security History

-- 1. Extend Users table for email verification & account lock
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS is_email_verified BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS email_verification_token VARCHAR(64),
    ADD COLUMN IF NOT EXISTS email_verification_expires_at TIMESTAMPTZ;

-- 2. Password History Table
CREATE TABLE IF NOT EXISTS password_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hashed_password VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS password_history_user_idx ON password_history(user_id);

-- 3. Refresh Tokens Table (with rotation & revocation)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) UNIQUE NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    replaced_by_token VARCHAR(64)
);

ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS is_revoked BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS replaced_by_token VARCHAR(64);

CREATE INDEX IF NOT EXISTS refresh_tokens_user_idx ON refresh_tokens(user_id);

-- 4. User Active Sessions Table (Device, IP, Browser, OS)
CREATE TABLE IF NOT EXISTS active_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_token VARCHAR(64) UNIQUE NOT NULL,
    device_type VARCHAR(50) DEFAULT 'Desktop',
    browser VARCHAR(100) DEFAULT 'Unknown Browser',
    operating_system VARCHAR(100) DEFAULT 'Unknown OS',
    ip_address VARCHAR(45) NOT NULL,
    geolocation VARCHAR(100),
    is_current BOOLEAN DEFAULT FALSE,
    last_active_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS active_sessions_user_idx ON active_sessions(user_id);

-- 5. Login Attempt Audit History
CREATE TABLE IF NOT EXISTS login_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NOT NULL,
    user_agent TEXT,
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS login_attempts_email_idx ON login_attempts(email);
