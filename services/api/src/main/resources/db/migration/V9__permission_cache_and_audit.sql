-- V9__permission_cache_and_audit.sql: Permission Cache & Audit Extension

-- 1. Extend Audit Logs table with permission code field
ALTER TABLE audit_logs
    ADD COLUMN IF NOT EXISTS permission_code VARCHAR(100);

-- 2. Index audit logs by permission code
CREATE INDEX IF NOT EXISTS audit_logs_permission_code_idx ON audit_logs(permission_code);
