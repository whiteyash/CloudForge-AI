-- V33__environment_management_platform.sql: Enterprise Environment Management Platform

CREATE TABLE IF NOT EXISTS environments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(150) NOT NULL,
    environment_type VARCHAR(50) NOT NULL,
    description TEXT,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    is_protected BOOLEAN DEFAULT FALSE,
    is_maintenance_mode BOOLEAN DEFAULT FALSE,
    is_frozen BOOLEAN DEFAULT FALSE,
    health_status VARCHAR(30) DEFAULT 'HEALTHY',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS environment_variables (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    environment_id UUID NOT NULL REFERENCES environments(id) ON DELETE CASCADE,
    key_name VARCHAR(100) NOT NULL,
    value VARCHAR(255) NOT NULL,
    is_secret BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS environment_targets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    environment_id UUID NOT NULL REFERENCES environments(id) ON DELETE CASCADE,
    target_name VARCHAR(100) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    connection_endpoint VARCHAR(255) NOT NULL
);
