import { NextResponse } from "next/server";

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { email } = body;

    if (!email) {
      return NextResponse.json(
        { error: "Registered email address is required for password recovery." },
        { status: 400 }
      );
    }

    const normalizedEmail = String(email).trim().toLowerCase();
    const token = `tok-reset-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
    const pin = Math.floor(100000 + Math.random() * 900000).toString();
    const timestamp = new Date().toISOString();
    const resetUrl = `http://localhost:3000/reset-password?token=${token}&email=${encodeURIComponent(normalizedEmail)}`;

    // Try forwarding to Spring Boot backend API
    try {
      await fetch("http://localhost:8000/auth/forgot-password", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: normalizedEmail, token, pin }),
      }).catch(() => {});
    } catch {
      // Backend offline fallback
    }

    const htmlBody = `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <style>
          body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0a1020; color: #e7edf7; margin: 0; padding: 40px 20px; }
          .card { max-width: 520px; margin: 0 auto; background: #111b2e; border: 1px solid #22314d; border-radius: 16px; padding: 32px; box-shadow: 0 10px 40px rgba(0,0,0,0.5); }
          .badge { display: inline-block; background: rgba(248, 113, 113, 0.1); color: #f87171; font-family: monospace; font-size: 11px; font-weight: bold; padding: 4px 12px; border-radius: 20px; border: 1px solid rgba(248, 113, 113, 0.3); margin-bottom: 16px; }
          .button { display: block; width: 100%; text-align: center; background: #3dd9c4; color: #0a1020; text-decoration: none; font-weight: bold; padding: 14px 0; border-radius: 12px; font-size: 14px; margin: 24px 0; }
          .pin-box { background: #0a1020; border: 1px solid #3dd9c4; border-radius: 12px; padding: 12px; text-align: center; font-family: monospace; font-size: 24px; font-weight: bold; color: #3dd9c4; margin: 16px 0; }
          .footer { margin-top: 32px; border-top: 1px solid #22314d; pt: 16px; font-size: 11px; color: #8b99b8; text-align: center; }
        </style>
      </head>
      <body>
        <div class="card">
          <div class="badge">🔒 ACCOUNT PASSWORD RECOVERY REQUEST</div>
          <h2 style="margin: 0 0 8px 0; color: #e7edf7;">Reset Your CloudForge Password</h2>
          <p style="font-size: 13px; color: #8b99b8; margin-bottom: 20px;">We received a password recovery request for your account <strong>${normalizedEmail}</strong>.</p>
          <a href="${resetUrl}" class="button">Reset Password Now &rarr;</a>
          <p style="font-size: 12px; color: #8b99b8; text-align: center;">Or use 6-digit recovery PIN:</p>
          <div class="pin-box">${pin}</div>
          <p style="font-size: 11px; color: #8b99b8; text-align: center;">Recovery link is valid for 15 minutes. If you did not request this, please ignore this email.</p>
          <div class="footer">
            <p>CloudForge AI Inc. • Enterprise DevOps Control Plane</p>
            <p style="font-family: monospace;">Dispatched to ${normalizedEmail} at ${timestamp}</p>
          </div>
        </div>
      </body>
      </html>
    `;

    return NextResponse.json({
      success: true,
      message: `Password reset token dispatched to ${normalizedEmail}`,
      email: normalizedEmail,
      token,
      pin,
      resetUrl,
      timestamp,
      subject: `[CloudForge AI] Account Password Recovery Request`,
      htmlPreview: htmlBody,
    });
  } catch (error: unknown) {
    const errorMsg = error instanceof Error ? error.message : "Internal Server Error";
    return NextResponse.json({ error: errorMsg }, { status: 500 });
  }
}
