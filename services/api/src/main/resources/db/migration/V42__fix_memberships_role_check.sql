-- V42__fix_memberships_role_check.sql: Update memberships role check constraint to support all enterprise roles

ALTER TABLE memberships DROP CONSTRAINT IF EXISTS memberships_role_check;

ALTER TABLE memberships ADD CONSTRAINT memberships_role_check
    CHECK (role IN ('OWNER', 'ADMIN', 'DEVELOPER', 'DEVOPS', 'SECURITY', 'VIEWER', 'ENGINEER'));
