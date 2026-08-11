-- V16__project_platform_modules_full.sql: Enterprise Project Platform Modules Full

-- 1. Extend Projects Table for Icon & Labels
ALTER TABLE projects
    ADD COLUMN IF NOT EXISTS icon_url VARCHAR(255),
    ADD COLUMN IF NOT EXISTS labels VARCHAR(255);

-- 2. Index Projects by Owner and Org
CREATE INDEX IF NOT EXISTS projects_org_owner_idx ON projects(org_id, owner_id);
