-- Flyway Migration V47: Secure Org Invitation Token Hashing & Email Delivery Tracking

ALTER TABLE org_invitations ADD COLUMN IF NOT EXISTS token_hash VARCHAR(64);
ALTER TABLE org_invitations ADD COLUMN IF NOT EXISTS delivery_status VARCHAR(30) DEFAULT 'PENDING';

CREATE INDEX IF NOT EXISTS idx_org_invitations_token_hash ON org_invitations(token_hash);
