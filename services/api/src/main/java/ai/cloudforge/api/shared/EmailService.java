package ai.cloudforge.api.shared;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private String mailHost;
    private int mailPort;
    private String mailUsername;
    private String mailPassword;
    private String fromAddress;
    private boolean useTls;
    private boolean enabled;
    private final String appBaseUrl;

    public EmailService(
            @Value("${cloudforge.mail.host:${MAIL_HOST:}}") String mailHost,
            @Value("${cloudforge.mail.port:${MAIL_PORT:587}}") int mailPort,
            @Value("${cloudforge.mail.username:${MAIL_USERNAME:}}") String mailUsername,
            @Value("${cloudforge.mail.password:${MAIL_PASSWORD:}}") String mailPassword,
            @Value("${cloudforge.mail.from:${MAIL_FROM:noreply@cloudforge.ai}}") String fromAddress,
            @Value("${cloudforge.mail.use-tls:true}") boolean useTls,
            @Value("${cloudforge.mail.enabled:true}") boolean enabled,
            @Value("${cloudforge.app.base-url:${APP_BASE_URL:http://localhost:3000}}") String appBaseUrl
    ) {
        this.mailHost = mailHost;
        this.mailPort = mailPort;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
        this.fromAddress = fromAddress;
        this.useTls = useTls;
        this.enabled = enabled;
        this.appBaseUrl = appBaseUrl;
    }

    public synchronized void updateSmtpConfig(String host, int port, String username, String password, String from, boolean tls, boolean isEnabled) {
        this.mailHost = host;
        this.mailPort = port;
        this.mailUsername = username;
        this.mailPassword = password;
        this.fromAddress = from;
        this.useTls = tls;
        this.enabled = isEnabled;
        log.info("EMAIL_SERVICE | Updated SMTP Config: host={}:{}, from={}, tls={}, enabled={}", host, port, from, tls, isEnabled);
    }

    public boolean isConfigured() {
        return enabled && mailHost != null && !mailHost.isBlank();
    }

    public JavaMailSenderImpl createSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailHost);
        sender.setPort(mailPort);

        if (mailUsername != null && !mailUsername.isBlank()) {
            sender.setUsername(mailUsername);
            sender.setPassword(mailPassword != null ? mailPassword : "");
        }

        Properties props = sender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", mailUsername != null && !mailUsername.isBlank() ? "true" : "false");

        if (useTls) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.trust", mailHost);
        }

        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        return sender;
    }

    public SmtpTestResult testConnection(String host, int port, String username, String password, boolean tls) {
        long startTime = System.currentTimeMillis();
        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost(host);
            sender.setPort(port);
            if (username != null && !username.isBlank()) {
                sender.setUsername(username);
                sender.setPassword(password != null ? password : "");
            }
            Properties props = sender.getJavaMailProperties();
            props.put("mail.transport.protocol", "smtp");
            props.put("mail.smtp.auth", username != null && !username.isBlank() ? "true" : "false");
            if (tls) {
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.ssl.trust", host);
            }
            props.put("mail.smtp.connectiontimeout", "3000");
            props.put("mail.smtp.timeout", "3000");

            sender.testConnection();
            long latency = System.currentTimeMillis() - startTime;
            return new SmtpTestResult(true, "SMTP Server EHLO Handshake Successful (" + latency + "ms)", latency);
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            return new SmtpTestResult(false, "SMTP Handshake Failed: " + e.getMessage(), latency);
        }
    }

    public SmtpDispatchResult sendInvitationEmail(String recipientEmail, String orgName, String role, String rawToken) {
        String invitationUrl = appBaseUrl + "/invitations/accept?token=" + rawToken;
        long startTime = System.currentTimeMillis();

        if (!isConfigured()) {
            log.warn("EMAIL_SERVICE [SMTP_NOT_CONFIGURED] | Recipient: {} | Org: {} | URL: {}", recipientEmail, orgName, invitationUrl);
            return new SmtpDispatchResult(
                    "SMTP_NOT_CONFIGURED",
                    "SMTP host not configured. Set MAIL_HOST, MAIL_USERNAME, and MAIL_PASSWORD environment variables or configure runtime SMTP server.",
                    mailHost != null && !mailHost.isBlank() ? mailHost : "not_configured",
                    mailPort,
                    0
            );
        }

        try {
            JavaMailSenderImpl sender = createSender();
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress != null && !fromAddress.isBlank() ? fromAddress : "noreply@cloudforge.ai");
            helper.setTo(recipientEmail);
            helper.setSubject("You've been invited to join " + orgName + " on CloudForge AI");

            String htmlBody = buildInvitationHtml(recipientEmail, orgName, role, invitationUrl);
            helper.setText(htmlBody, true);

            sender.send(message);
            long latency = System.currentTimeMillis() - startTime;
            log.info("EMAIL_SERVICE [SMTP_DISPATCH_SUCCESS] | Outbound SMTP MIME Email Accepted by {}:{} for {} in {}ms",
                    mailHost, mailPort, recipientEmail, latency);

            return new SmtpDispatchResult(
                    "SENT",
                    "Real HTML invitation accepted by SMTP server (" + mailHost + ":" + mailPort + ")",
                    mailHost,
                    mailPort,
                    latency
            );
        } catch (Exception e) {
            long latency = System.currentTimeMillis() - startTime;
            log.error("EMAIL_SERVICE [SMTP_DISPATCH_FAILED] | Recipient: {} | Host: {}:{} | Reason: {} | Latency: {}ms",
                    recipientEmail, mailHost, mailPort, e.getMessage(), latency);

            return new SmtpDispatchResult(
                    "FAILED",
                    "SMTP Dispatch Failed (" + mailHost + ":" + mailPort + "): " + e.getMessage(),
                    mailHost,
                    mailPort,
                    latency
            );
        }
    }

    public String buildInvitationHtml(String recipientEmail, String orgName, String role, String invitationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="utf-8"/></head>
            <body style="background-color:#060A14; color:#E7EDF7; font-family:sans-serif; padding:40px 20px;">
              <div style="max-width:560px; margin:0 auto; background:#0B132B; border:1px solid #22314D; border-radius:24px; padding:32px; box-shadow:0 0 40px rgba(61,217,196,0.15);">
                <div style="display:flex; align-items:center; gap:12px; margin-bottom:24px;">
                  <h1 style="color:#3DD9C4; margin:0; font-size:22px; font-weight:800; tracking-tight: -0.5px;">CloudForge AI</h1>
                </div>
                <h2 style="font-size:18px; color:#E7EDF7; margin-top:0;">You're Invited to Join %s</h2>
                <p style="font-size:14px; color:#8B99B8; line-height:1.6;">
                  Hello <strong style="color:#E7EDF7;">%s</strong>,<br/>
                  You have been assigned the role of <strong style="color:#3DD9C4;">%s</strong> in the <strong>%s</strong> CloudForge AI workspace.
                </p>
                <div style="margin:28px 0; text-align:center;">
                  <a href="%s" style="background:#3DD9C4; color:#0A1020; text-decoration:none; padding:12px 28px; border-radius:12px; font-weight:800; font-size:14px; display:inline-block;">Accept Workspace Invitation &rarr;</a>
                </div>
                <p style="font-size:12px; color:#8B99B8; line-height:1.5;">Or copy & paste this magic URL into your browser:<br/>
                  <code style="color:#3DD9C4; word-break:break-all;">%s</code>
                </p>
                <hr style="border:none; border-top:1px solid #22314D; margin:24px 0;"/>
                <p style="font-size:11px; color:#8B99B8; margin:0;">This invitation token expires in 48 hours. If you did not expect this invitation, you can safely ignore this email.</p>
              </div>
            </body>
            </html>
            """.formatted(orgName, recipientEmail, role, orgName, invitationUrl, invitationUrl);
    }

    public static record SmtpDispatchResult(
            String status, // SENT | FAILED | SMTP_NOT_CONFIGURED
            String message,
            String smtpHost,
            int smtpPort,
            long latencyMs
    ) {}

    public static class SmtpTestResult {
        public boolean success;
        public String message;
        public long latencyMs;

        public SmtpTestResult(boolean success, String message, long latencyMs) {
            this.success = success;
            this.message = message;
            this.latencyMs = latencyMs;
        }
    }
}
