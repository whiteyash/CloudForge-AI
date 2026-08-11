# CloudForge AI — Authentication Architecture

## Overview
CloudForge AI implements an enterprise-grade authentication platform combining stateless JWT access tokens with single-use refresh token rotation stored in PostgreSQL/Redis.

### Security Guarantees
- **HMAC-SHA256 Signed JWTs**: Access tokens carry user identity, organization context, and active workspace claims.
- **Refresh Token Rotation**: Every refresh invocation revokes the prior refresh token and issues a new cryptographically generated token.
- **Brute-Force Lockout**: Automatic account lockout after 5 consecutive failed login attempts within a 15-minute window.
- **Session Revocation**: Remote session invalidation across device logins via Security Center.
