-- Flyway Migration V46: Incident Response, Intelligent On-Call Dispatcher & PagerDuty/Slack Integration Engine

CREATE TABLE IF NOT EXISTS incidents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    organization_id UUID NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT NULL,
    severity VARCHAR(50) NOT NULL DEFAULT 'SEV_3_MEDIUM',
    status VARCHAR(30) NOT NULL DEFAULT 'TRIGGERED',
    root_cause VARCHAR(255) NULL,
    confidence_score DOUBLE PRECISION NULL,
    assignee_user_id UUID NULL,
    incident_source VARCHAR(50) NULL DEFAULT 'SYSTEM',
    triggered_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    acknowledged_at TIMESTAMP WITH TIME ZONE NULL,
    resolved_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE TABLE IF NOT EXISTS oncall_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    time_zone VARCHAR(50) NOT NULL DEFAULT 'UTC',
    rotation_type VARCHAR(30) NOT NULL DEFAULT 'WEEKLY',
    active_user_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS alert_integration_channels (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL,
    channel_name VARCHAR(100) NOT NULL,
    channel_type VARCHAR(30) NOT NULL DEFAULT 'PAGERDUTY',
    webhook_url VARCHAR(500) NULL,
    api_key_encrypted VARCHAR(500) NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_incidents_project ON incidents(project_id);
CREATE INDEX IF NOT EXISTS idx_incidents_org ON incidents(organization_id);
CREATE INDEX IF NOT EXISTS idx_incidents_status ON incidents(status);
CREATE INDEX IF NOT EXISTS idx_oncall_schedules_org ON oncall_schedules(organization_id);
CREATE INDEX IF NOT EXISTS idx_alert_channels_org ON alert_integration_channels(organization_id);
