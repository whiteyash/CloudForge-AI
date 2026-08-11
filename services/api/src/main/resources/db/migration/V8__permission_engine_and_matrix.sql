-- V8__permission_engine_and_matrix.sql: Centralized Permission Engine Catalog & Matrix

-- 1. Permissions Registry Catalog
CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(100) UNIQUE NOT NULL,
    module VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2. Role to Permission Mapping Matrix
CREATE TABLE IF NOT EXISTS role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role VARCHAR(40) NOT NULL,
    permission_code VARCHAR(100) NOT NULL REFERENCES permissions(code) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uk_role_permission UNIQUE (role, permission_code)
);

CREATE INDEX IF NOT EXISTS role_permissions_role_idx ON role_permissions(role);

-- Seed Permissions Catalog
INSERT INTO permissions (code, module, description) VALUES
('organization.view', 'Organization', 'View organization details and metadata'),
('organization.update', 'Organization', 'Update organization name, website, and branding settings'),
('organization.delete', 'Organization', 'Soft delete organization workspace'),
('organization.archive', 'Organization', 'Archive organization to read-only mode'),
('organization.restore', 'Organization', 'Restore archived organization'),
('organization.transfer', 'Organization', 'Transfer organization ownership to another user'),

('member.invite', 'Members', 'Issue invitation tokens to new members'),
('member.remove', 'Members', 'Remove members from organization'),
('member.update', 'Members', 'Update member profile details'),
('member.view', 'Members', 'View organization member roster'),
('member.role.change', 'Members', 'Upgrade or downgrade member RBAC roles'),

('team.create', 'Teams', 'Create new cross-functional team squad'),
('team.update', 'Teams', 'Update team details and descriptions'),
('team.delete', 'Teams', 'Delete team workspace'),
('team.assign', 'Teams', 'Assign or remove members from team squad'),

('project.create', 'Projects', 'Create new project workspace'),
('project.update', 'Projects', 'Update project configuration and repository settings'),
('project.delete', 'Projects', 'Delete project workspace'),

('audit.view', 'Audit', 'View organization audit logs and event timeline'),
('audit.export', 'Audit', 'Export audit logs to CSV / SIEM target'),

('settings.manage', 'Settings', 'Manage global security and integration settings'),
('subscription.view', 'Subscription', 'View organization subscription plan limits and usage'),
('subscription.manage', 'Subscription', 'Upgrade or modify subscription tiers'),
('session.manage', 'Security', 'Manage active device sessions and terminate sessions')
ON CONFLICT (code) DO NOTHING;

-- Seed Role-Permission Mappings for OWNER
INSERT INTO role_permissions (role, permission_code) VALUES
('OWNER', 'organization.view'), ('OWNER', 'organization.update'), ('OWNER', 'organization.delete'),
('OWNER', 'organization.archive'), ('OWNER', 'organization.restore'), ('OWNER', 'organization.transfer'),
('OWNER', 'member.invite'), ('OWNER', 'member.remove'), ('OWNER', 'member.update'), ('OWNER', 'member.view'), ('OWNER', 'member.role.change'),
('OWNER', 'team.create'), ('OWNER', 'team.update'), ('OWNER', 'team.delete'), ('OWNER', 'team.assign'),
('OWNER', 'project.create'), ('OWNER', 'project.update'), ('OWNER', 'project.delete'),
('OWNER', 'audit.view'), ('OWNER', 'audit.export'), ('OWNER', 'settings.manage'),
('OWNER', 'subscription.view'), ('OWNER', 'subscription.manage'), ('OWNER', 'session.manage')
ON CONFLICT DO NOTHING;

-- Seed Role-Permission Mappings for ADMIN
INSERT INTO role_permissions (role, permission_code) VALUES
('ADMIN', 'organization.view'), ('ADMIN', 'organization.update'), ('ADMIN', 'organization.archive'),
('ADMIN', 'member.invite'), ('ADMIN', 'member.remove'), ('ADMIN', 'member.view'),
('ADMIN', 'team.create'), ('ADMIN', 'team.update'), ('ADMIN', 'team.delete'), ('ADMIN', 'team.assign'),
('ADMIN', 'project.create'), ('ADMIN', 'project.update'), ('ADMIN', 'audit.view'),
('ADMIN', 'subscription.view'), ('ADMIN', 'session.manage')
ON CONFLICT DO NOTHING;

-- Seed Role-Permission Mappings for DEVELOPER
INSERT INTO role_permissions (role, permission_code) VALUES
('DEVELOPER', 'organization.view'), ('DEVELOPER', 'member.view'),
('DEVELOPER', 'project.create'), ('DEVELOPER', 'project.update'),
('DEVELOPER', 'audit.view'), ('DEVELOPER', 'subscription.view')
ON CONFLICT DO NOTHING;

-- Seed Role-Permission Mappings for VIEWER
INSERT INTO role_permissions (role, permission_code) VALUES
('VIEWER', 'organization.view'), ('VIEWER', 'member.view'),
('VIEWER', 'audit.view'), ('VIEWER', 'subscription.view')
ON CONFLICT DO NOTHING;
