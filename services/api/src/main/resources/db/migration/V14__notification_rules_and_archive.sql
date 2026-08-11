-- V14__notification_rules_and_archive.sql: Notification Rules & Expiration Schema

-- 1. Notification Rules Table
CREATE TABLE IF NOT EXISTS notification_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    org_id UUID REFERENCES organizations(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    channel VARCHAR(50) DEFAULT 'IN_APP',
    is_suppressed BOOLEAN DEFAULT FALSE,
    min_severity VARCHAR(20) DEFAULT 'INFO',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. Index Notification Rules
CREATE INDEX IF NOT EXISTS notification_rules_user_event_idx ON notification_rules(user_id, event_type);
