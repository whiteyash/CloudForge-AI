-- V15__enterprise_project_platform_full.sql: Enterprise Project Platform Schema

-- 1. Project Variables Table
CREATE TABLE IF NOT EXISTS project_variables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    environment_id UUID REFERENCES project_environments(id) ON DELETE CASCADE,
    var_key VARCHAR(100) NOT NULL,
    var_value TEXT NOT NULL,
    is_masked BOOLEAN DEFAULT FALSE,
    is_protected BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_project_env_key UNIQUE (project_id, environment_id, var_key)
);

-- 2. Secret References Table (Vault Ready)
CREATE TABLE IF NOT EXISTS project_secret_references (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    secret_name VARCHAR(100) NOT NULL,
    vault_path VARCHAR(255) NOT NULL,
    vault_key VARCHAR(100) NOT NULL,
    scope VARCHAR(50) DEFAULT 'ALL_ENVIRONMENTS',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. Favorite Projects Table
CREATE TABLE IF NOT EXISTS favorite_projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_user_favorite_project UNIQUE (user_id, project_id)
);
