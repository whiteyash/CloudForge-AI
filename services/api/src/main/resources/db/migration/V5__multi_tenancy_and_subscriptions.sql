-- V5__multi_tenancy_and_subscriptions.sql: Multi-Tenancy, Subscriptions, and Organization Branding

-- 1. Extend Organizations table
ALTER TABLE organizations
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS logo_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS website_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS timezone VARCHAR(50) DEFAULT 'UTC',
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS primary_color VARCHAR(10) DEFAULT '#3DD9C4',
    ADD COLUMN IF NOT EXISTS member_approval_policy VARCHAR(30) DEFAULT 'ANY_ADMIN',
    ADD COLUMN IF NOT EXISTS deleted_at TIMESTAMPTZ;

-- 2. Organization Subscriptions Table
CREATE TABLE IF NOT EXISTS org_subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    plan_tier VARCHAR(30) NOT NULL DEFAULT 'FREE',
    seat_limit INT NOT NULL DEFAULT 5,
    storage_limit_gb INT NOT NULL DEFAULT 10,
    api_rate_limit_per_min INT NOT NULL DEFAULT 1000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    current_period_start TIMESTAMPTZ NOT NULL DEFAULT now(),
    current_period_end TIMESTAMPTZ NOT NULL DEFAULT (now() + INTERVAL '30 days'),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX IF NOT EXISTS org_subscriptions_org_idx ON org_subscriptions(org_id);

-- 3. Tenant Usage Tracking Table
CREATE TABLE IF NOT EXISTS org_usage_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    org_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    metric_name VARCHAR(50) NOT NULL,
    metric_value BIGINT NOT NULL DEFAULT 0,
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS org_usage_metrics_org_idx ON org_usage_metrics(org_id, metric_name);
