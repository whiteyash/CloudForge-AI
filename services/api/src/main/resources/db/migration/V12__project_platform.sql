-- V12__project_platform.sql: Enterprise Project Platform

-- 1. Extend Projects Table
ALTER TABLE projects
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS tags VARCHAR(255),
    ADD COLUMN IF NOT EXISTS owner_id UUID REFERENCES users(id) ON DELETE SET NULL,
    ADD COLUMN IF NOT EXISTS visibility VARCHAR(20) DEFAULT 'PRIVATE',
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) DEFAULT 'ACTIVE',
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT now();

-- 2. Project Environments Table
CREATE TABLE IF NOT EXISTS project_environments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(50) NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'DEV',
    k8s_namespace VARCHAR(100),
    cluster_name VARCHAR(100) DEFAULT 'primary-cluster',
    auto_deploy BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 3. Project Repositories Linkage
CREATE TABLE IF NOT EXISTS project_repositories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    provider VARCHAR(20) NOT NULL DEFAULT 'GITHUB',
    repo_url VARCHAR(255) NOT NULL,
    default_branch VARCHAR(50) DEFAULT 'main',
    is_connected BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 4. Project Members & Roles
CREATE TABLE IF NOT EXISTS project_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    project_role VARCHAR(50) NOT NULL DEFAULT 'DEVELOPER',
    added_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_project_member UNIQUE (project_id, user_id)
);
