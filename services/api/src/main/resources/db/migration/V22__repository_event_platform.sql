-- V22__repository_event_platform.sql: Enterprise Repository Event Platform

-- 1. Repository Webhooks Table
CREATE TABLE IF NOT EXISTS repository_webhooks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    connection_id UUID REFERENCES git_provider_connections(id) ON DELETE SET NULL,
    provider_name VARCHAR(50) NOT NULL,
    target_url VARCHAR(255) NOT NULL,
    secret VARCHAR(255) NOT NULL,
    events VARCHAR(255) DEFAULT 'push,pull_request',
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. Repository Events Table
CREATE TABLE IF NOT EXISTS repository_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    repository_id UUID REFERENCES imported_repositories(id) ON DELETE SET NULL,
    provider_name VARCHAR(50) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    delivery_id VARCHAR(100) NOT NULL,
    correlation_id UUID NOT NULL DEFAULT gen_random_uuid(),
    payload_hash VARCHAR(64) NOT NULL,
    status VARCHAR(30) DEFAULT 'RECEIVED',
    attempt_count INT DEFAULT 1,
    failure_reason TEXT,
    received_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    processed_at TIMESTAMPTZ
);

-- 3. Webhook Deliveries Table
CREATE TABLE IF NOT EXISTS webhook_deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    webhook_id UUID REFERENCES repository_webhooks(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    status_code INT NOT NULL,
    duration_ms INT DEFAULT 0,
    delivered_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 4. Dead Letter Events Table
CREATE TABLE IF NOT EXISTS dead_letter_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id UUID NOT NULL REFERENCES repository_events(id) ON DELETE CASCADE,
    reason TEXT NOT NULL,
    failed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
