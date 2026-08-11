-- V13__enterprise_event_and_notification_platform.sql: Enterprise Event, Notification & Audit Platform

-- 1. Extend Notifications Table
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS category VARCHAR(50) DEFAULT 'SYSTEM',
    ADD COLUMN IF NOT EXISTS priority VARCHAR(20) DEFAULT 'NORMAL',
    ADD COLUMN IF NOT EXISTS severity VARCHAR(20) DEFAULT 'INFO',
    ADD COLUMN IF NOT EXISTS correlation_id UUID DEFAULT gen_random_uuid(),
    ADD COLUMN IF NOT EXISTS expires_at TIMESTAMPTZ,
    ADD COLUMN IF NOT EXISTS is_archived BOOLEAN DEFAULT FALSE;

CREATE INDEX IF NOT EXISTS notifications_user_status_idx ON notifications(user_id, status);

-- 2. Notification Templates Table
CREATE TABLE IF NOT EXISTS notification_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) UNIQUE NOT NULL,
    title_template VARCHAR(255) NOT NULL,
    message_template TEXT NOT NULL,
    category VARCHAR(50) DEFAULT 'SYSTEM',
    default_priority VARCHAR(20) DEFAULT 'NORMAL',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. Audit Trail Schema Extension
ALTER TABLE audit_logs
    ADD COLUMN IF NOT EXISTS correlation_id UUID DEFAULT gen_random_uuid(),
    ADD COLUMN IF NOT EXISTS category VARCHAR(50) DEFAULT 'GENERAL',
    ADD COLUMN IF NOT EXISTS severity VARCHAR(20) DEFAULT 'INFO',
    ADD COLUMN IF NOT EXISTS ip_address VARCHAR(45),
    ADD COLUMN IF NOT EXISTS user_agent TEXT,
    ADD COLUMN IF NOT EXISTS metadata_json TEXT;

CREATE INDEX IF NOT EXISTS audit_logs_correlation_idx ON audit_logs(correlation_id);
