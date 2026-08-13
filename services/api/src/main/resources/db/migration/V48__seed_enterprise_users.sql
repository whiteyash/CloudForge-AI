-- V48__seed_enterprise_users.sql: Seed Default Production Enterprise Accounts
-- Password for both seeded accounts: password123 (BCrypt: $2a$12$b.uYmZ64g3K2D9H8cKk/oO3y0nZk/J3g9B1e5X7k9P1Q2R3S4T5U6)

INSERT INTO users (id, email, hashed_password, full_name, is_email_verified, created_at)
VALUES 
    ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'admin@cloudforge.ai', '$2a$12$R.SjC.wGk8YtF2B2p5X8v.aH1J9K2L3M4N5O6P7Q8R9S0T1U2V3W4', 'Platform Engineer', true, now()),
    ('b0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22', 'developer@cloudforge.ai', '$2a$12$R.SjC.wGk8YtF2B2p5X8v.aH1J9K2L3M4N5O6P7Q8R9S0T1U2V3W4', 'Lead Developer', true, now())
ON CONFLICT (email) DO NOTHING;

-- Seed Default Organization & Memberships
INSERT INTO organizations (id, name, slug, created_at)
VALUES ('c0eebc99-9c0b-4ef8-bb6d-6bb9bd380a33', 'CloudForge System', 'cloudforge-system', now())
ON CONFLICT (slug) DO NOTHING;

INSERT INTO memberships (org_id, user_id, role, created_at)
SELECT o.id, u.id, 'OWNER', now()
FROM organizations o, users u
WHERE o.slug = 'cloudforge-system' AND u.email = 'admin@cloudforge.ai'
ON CONFLICT (org_id, user_id) DO NOTHING;

INSERT INTO memberships (org_id, user_id, role, created_at)
SELECT o.id, u.id, 'DEVELOPER', now()
FROM organizations o, users u
WHERE o.slug = 'cloudforge-system' AND u.email = 'developer@cloudforge.ai'
ON CONFLICT (org_id, user_id) DO NOTHING;
