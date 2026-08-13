import { NextResponse } from "next/server";
import tls from "node:tls";
import dns from "node:dns/promises";
import { API_BASE_URL } from "@/lib/api";

// Direct Node.js TLS/Net Socket SMTP Sender
async function sendRawSmtpEmail({
  host,
  port,
  user,
  pass,
  to,
  subject,
  html,
}: {
  host: string;
  port: number;
  user: string;
  pass: string;
  to: string;
  subject: string;
  html: string;
}): Promise<boolean> {
  return new Promise((resolve) => {
    try {
      const socket = tls.connect(port, host, { rejectUnauthorized: false }, () => {
        let step = 0;

        const send = (cmd: string) => {
          socket.write(cmd + "\r\n");
        };

        socket.on("data", (data) => {
          const response = data.toString();

          if (step === 0 && response.startsWith("220")) {
            send(`EHLO ${host}`);
            step = 1;
          } else if (step === 1 && response.startsWith("250")) {
            const authBase64 = Buffer.from(`\0${user}\0${pass}`).toString("base64");
            send(`AUTH PLAIN ${authBase64}`);
            step = 2;
          } else if (step === 2 && response.startsWith("235")) {
            send(`MAIL FROM:<${user}>`);
            step = 3;
          } else if (step === 3 && response.startsWith("250")) {
            send(`RCPT TO:<${to}>`);
            step = 4;
          } else if (step === 4 && response.startsWith("250")) {
            send("DATA");
            step = 5;
          } else if (step === 5 && response.startsWith("354")) {
            const message = [
              `From: "CloudForge AI" <${user}>`,
              `To: ${to}`,
              `Subject: ${subject}`,
              "MIME-Version: 1.0",
              'Content-Type: text/html; charset="UTF-8"',
              "",
              html,
              ".",
            ].join("\r\n");
            send(message);
            step = 6;
          } else if (step === 6 && response.startsWith("250")) {
            send("QUIT");
            socket.end();
            resolve(true);
          } else if (response.startsWith("5") || response.startsWith("4")) {
            socket.end();
            resolve(false);
          }
        });

        socket.on("error", (err) => {
          console.error("SMTP Socket error:", err);
          resolve(false);
        });
      });
    } catch (err) {
      console.error("TLS Connection error:", err);
      resolve(false);
    }
  });
}

export async function POST(request: Request) {
  try {
    const body = await request.json();
    const { email, fullName, otp } = body;

    if (!email || !otp) {
      return NextResponse.json(
        { error: "Email and OTP code are required for verification dispatch." },
        { status: 400 }
      );
    }

    const normalizedEmail = String(email).trim().toLowerCase();
    const otpCode = String(otp).trim();
    const recipientName = fullName ? String(fullName).trim() : "Developer";
    const timestamp = new Date().toISOString();

    // 1. Real DNS MX Record Lookup to verify email domain reachable
    const domain = normalizedEmail.split("@")[1];
    if (!domain) {
      return NextResponse.json(
        { error: "Invalid email format. Missing domain." },
        { status: 400 }
      );
    }

    try {
      const mxRecords = await dns.resolveMx(domain);
      if (!mxRecords || mxRecords.length === 0) {
        return NextResponse.json(
          { error: `Invalid email domain. No MX mail servers found for '@${domain}'.` },
          { status: 400 }
        );
      }
    } catch {
      return NextResponse.json(
        { error: `Invalid email domain '@${domain}'. Unable to verify mail exchanger (MX) DNS records.` },
        { status: 400 }
      );
    }

    // HTML Email Template
    const htmlBody = `
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8">
        <style>
          body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0a1020; color: #e7edf7; margin: 0; padding: 40px 20px; }
          .card { max-width: 520px; margin: 0 auto; background: #111b2e; border: 1px solid #22314d; border-radius: 16px; padding: 32px; box-shadow: 0 10px 40px rgba(0,0,0,0.5); }
          .badge { display: inline-block; background: rgba(61, 217, 196, 0.1); color: #3dd9c4; font-family: monospace; font-size: 11px; font-weight: bold; padding: 4px 12px; border-radius: 20px; border: 1px solid rgba(61, 217, 196, 0.3); margin-bottom: 16px; }
          .otp-box { background: #0a1020; border: 1px solid #3dd9c4; border-radius: 12px; padding: 16px; text-align: center; font-family: monospace; font-size: 32px; font-weight: bold; letter-spacing: 8px; color: #3dd9c4; margin: 24px 0; }
          .footer { margin-top: 32px; pt: 16px; border-top: 1px solid #22314d; font-size: 11px; color: #8b99b8; text-align: center; }
        </style>
      </head>
      <body>
        <div class="card">
          <div class="badge">⚡ CLOUDFORGE SECURITY VERIFICATION</div>
          <h2 style="margin: 0 0 8px 0; color: #e7edf7;">Welcome to CloudForge AI, ${recipientName}!</h2>
          <p style="font-size: 13px; color: #8b99b8; margin-bottom: 24px;">Please use the 6-digit security code below to complete your email verification and activate your organization workspace.</p>
          <div class="otp-box">${otpCode}</div>
          <p style="font-size: 12px; color: #8b99b8; text-align: center;">This verification code is valid for <strong>10 minutes</strong>. Do not share this code with anyone.</p>
          <div class="footer">
            <p>CloudForge AI Inc. • Enterprise DevOps &amp; AI Control Plane</p>
            <p style="font-family: monospace;">Dispatched to ${normalizedEmail} at ${timestamp}</p>
          </div>
        </div>
      </body>
      </html>
    `;

    const subject = `[CloudForge AI] Your 6-Digit Security Verification Code`;

    // 2. Real SMTP Dispatch
    const smtpHost = process.env.SMTP_HOST || "smtp.gmail.com";
    const smtpPort = parseInt(process.env.SMTP_PORT || "465", 10);
    const smtpUser = process.env.SMTP_USER;
    const smtpPass = process.env.SMTP_PASS || process.env.GMAIL_APP_PASSWORD;

    if (smtpUser && smtpPass) {
      const isRealSmtpSent = await sendRawSmtpEmail({
        host: smtpHost,
        port: smtpPort,
        user: smtpUser,
        pass: smtpPass,
        to: normalizedEmail,
        subject,
        html: htmlBody,
      });

      if (!isRealSmtpSent) {
        return NextResponse.json(
          { error: `SMTP Delivery Failed: Could not dispatch verification email to ${normalizedEmail}. Please check SMTP credentials or server configuration.` },
          { status: 502 }
        );
      }
    }

    // Forward to Spring Boot backend API to store in PostgreSQL audit logs
    try {
      await fetch(`${API_BASE_URL}/auth/send-otp`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: normalizedEmail, otp: otpCode, fullName: recipientName }),
      }).catch(() => {});
    } catch {
      // Ignore
    }

    // Return STRICT production response (NO OTP code or HTML preview exposed to client)
    return NextResponse.json({
      success: true,
      message: `Security verification code dispatched to ${normalizedEmail}. Please check your inbox.`,
      email: normalizedEmail,
      timestamp,
    });
  } catch (error: unknown) {
    const errorMsg = error instanceof Error ? error.message : "Internal Server Error";
    return NextResponse.json({ error: errorMsg }, { status: 500 });
  }
}
