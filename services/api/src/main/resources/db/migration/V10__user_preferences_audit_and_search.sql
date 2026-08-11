-- V10__user_preferences_audit_and_search.sql: User Preferences, Notification Preferences & Search History

-- 1. User Preferences Table
CREATE TABLE IF NOT EXISTS user_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    date_format VARCHAR(20) DEFAULT 'YYYY-MM-DD',
    time_format VARCHAR(10) DEFAULT '24h',
    theme VARCHAR(20) DEFAULT 'DARK_SLATE',
    accent_color VARCHAR(20) DEFAULT '#3DD9C4',
    density VARCHAR(20) DEFAULT 'COMFORTABLE',
    default_workspace_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
    default_landing_page VARCHAR(100) DEFAULT '/',
    sidebar_collapsed BOOLEAN DEFAULT FALSE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. Notification Preferences Table
CREATE TABLE IF NOT EXISTS notification_preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    email_security_alerts BOOLEAN DEFAULT TRUE,
    email_org_events BOOLEAN DEFAULT TRUE,
    email_invitations BOOLEAN DEFAULT TRUE,
    email_role_changes BOOLEAN DEFAULT TRUE,
    inapp_security_alerts BOOLEAN DEFAULT TRUE,
    inapp_org_events BOOLEAN DEFAULT TRUE,
    inapp_invitations BOOLEAN DEFAULT TRUE,
    inapp_role_changes BOOLEAN DEFAULT TRUE,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. Favorite / Pinned Workspaces
CREATE TABLE IF NOT EXISTS favorite_workspaces (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    org_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    is_pinned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_favorite_org UNIQUE (user_id, org_id)
);

-- 4. Recent Search Queries Log
CREATE TABLE IF NOT EXISTS recent_searches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    query_text VARCHAR(255) NOT NULL,
    searched_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS recent_searches_user_idx ON recent_searches(user_id);
