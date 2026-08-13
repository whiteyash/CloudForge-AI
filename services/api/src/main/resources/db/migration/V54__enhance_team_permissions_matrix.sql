-- V54__enhance_team_permissions_matrix.sql: Seed fine-grained team permission catalog & matrix

INSERT INTO permissions (code, module, description) VALUES
('team.list', 'Teams', 'List all team squads in organization'),
('team.view', 'Teams', 'View team details and member roster'),
('team.create', 'Teams', 'Create new team squad'),
('team.update', 'Teams', 'Update team details and descriptions'),
('team.delete', 'Teams', 'Delete or archive team squad'),
('team.member.add', 'Teams', 'Add member to team squad'),
('team.member.remove', 'Teams', 'Remove member from team squad'),
('team.member.role.change', 'Teams', 'Update member role within team squad')
ON CONFLICT (code) DO NOTHING;

-- Mappings for OWNER
INSERT INTO role_permissions (role, permission_code) VALUES
('OWNER', 'team.list'),
('OWNER', 'team.view'),
('OWNER', 'team.create'),
('OWNER', 'team.update'),
('OWNER', 'team.delete'),
('OWNER', 'team.member.add'),
('OWNER', 'team.member.remove'),
('OWNER', 'team.member.role.change')
ON CONFLICT (role, permission_code) DO NOTHING;

-- Mappings for ADMIN
INSERT INTO role_permissions (role, permission_code) VALUES
('ADMIN', 'team.list'),
('ADMIN', 'team.view'),
('ADMIN', 'team.create'),
('ADMIN', 'team.update'),
('ADMIN', 'team.delete'),
('ADMIN', 'team.member.add'),
('ADMIN', 'team.member.remove'),
('ADMIN', 'team.member.role.change')
ON CONFLICT (role, permission_code) DO NOTHING;

-- Mappings for LEAD
INSERT INTO role_permissions (role, permission_code) VALUES
('LEAD', 'team.list'),
('LEAD', 'team.view'),
('LEAD', 'team.create'),
('LEAD', 'team.update'),
('LEAD', 'team.member.add'),
('LEAD', 'team.member.remove')
ON CONFLICT (role, permission_code) DO NOTHING;

-- Mappings for DEVELOPER
INSERT INTO role_permissions (role, permission_code) VALUES
('DEVELOPER', 'team.list'),
('DEVELOPER', 'team.view')
ON CONFLICT (role, permission_code) DO NOTHING;

-- Mappings for VIEWER
INSERT INTO role_permissions (role, permission_code) VALUES
('VIEWER', 'team.list'),
('VIEWER', 'team.view')
ON CONFLICT (role, permission_code) DO NOTHING;
