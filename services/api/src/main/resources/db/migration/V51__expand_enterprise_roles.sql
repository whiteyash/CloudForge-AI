-- V51__expand_enterprise_roles.sql: Expand memberships and invitations role check constraints to support 11+ enterprise roles

ALTER TABLE memberships DROP CONSTRAINT IF EXISTS memberships_role_check;

ALTER TABLE memberships ADD CONSTRAINT memberships_role_check
    CHECK (role IN ('OWNER', 'ADMIN', 'LEAD', 'DEVELOPER', 'DEVOPS', 'SECURITY', 'DATA_ENGINEER', 'QA_TESTER', 'PRODUCT_MGR', 'AUDITOR', 'VIEWER', 'ENGINEER'));

ALTER TABLE org_invitations DROP CONSTRAINT IF EXISTS org_invitations_role_check;

ALTER TABLE org_invitations ADD CONSTRAINT org_invitations_role_check
    CHECK (role IN ('OWNER', 'ADMIN', 'LEAD', 'DEVELOPER', 'DEVOPS', 'SECURITY', 'DATA_ENGINEER', 'QA_TESTER', 'PRODUCT_MGR', 'AUDITOR', 'VIEWER', 'ENGINEER'));
