-- V41__human_approved_operations.sql: Human-Approved Autonomous Operations Platform

CREATE TABLE IF NOT EXISTS remediation_plans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    target_type VARCHAR(50) NOT NULL,
    summary TEXT NOT NULL,
    confidence INT NOT NULL DEFAULT 95,
    evidence TEXT NOT NULL,
    risk_assessment TEXT NOT NULL,
    rollback_plan TEXT NOT NULL,
    estimated_impact VARCHAR(255) NOT NULL,
    required_permissions VARCHAR(255) NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING_APPROVAL',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS approval_requests (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id UUID NOT NULL REFERENCES remediation_plans(id) ON DELETE CASCADE,
    requested_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(30) DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS approval_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    request_id UUID NOT NULL REFERENCES approval_requests(id) ON DELETE CASCADE,
    approved_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    action VARCHAR(30) NOT NULL,
    comments TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS execution_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    plan_id UUID NOT NULL REFERENCES remediation_plans(id) ON DELETE CASCADE,
    executed_by UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    execution_service VARCHAR(100) NOT NULL,
    status VARCHAR(30) NOT NULL,
    logs TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
