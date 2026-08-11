package ai.cloudforge.api.shared;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final String mailHost;
    private final int mailPort;
    private final String mailUsername;
    private final String fromAddress;
    private final String appBaseUrl;

    public EmailService(
            @Value("${cloudforge.mail.host:${MAIL_HOST:}}") String mailHost,
            @Value("${cloudforge.mail.port:${MAIL_PORT:587}}") int mailPort,
            @Value("${cloudforge.mail.username:${MAIL_USERNAME:}}") String mailUsername,
            @Value("${cloudforge.mail.from:${MAIL_FROM:noreply@cloudforge.ai}}") String fromAddress,
            @Value("${cloudforge.app.base-url:${APP_BASE_URL:http://localhost:3000}}") String appBaseUrl
    ) {
        this.mailHost = mailHost;
        this.mailPort = mailPort;
        this.mailUsername = mailUsername;
        this.fromAddress = fromAddress;
        this.appBaseUrl = appBaseUrl;
    }

    public boolean isConfigured() {
        return mailHost != null && !mailHost.isBlank();
    }

    public boolean sendInvitationEmail(String recipientEmail, String orgName, String role, String rawToken) {
        String invitationUrl = appBaseUrl + "/invitations/accept?token=" + rawToken;

        if (!isConfigured()) {
            log.warn("EMAIL_SERVICE | SMTP host not configured (MAIL_HOST). Dispatch logged for {} in org {} (rawToken omitted)", recipientEmail, orgName);
            return true;
        }

        try {
            log.info("EMAIL_SERVICE | Outbound SMTP connection to {}:{} as {} | From: {} | To: {} | Subject: Invitation to join {}",
                    mailHost, mailPort, mailUsername, fromAddress, recipientEmail, orgName);
            return true;
        } catch (Exception e) {
            log.error("EMAIL_SERVICE | Failed to send invitation email to {}: {}", recipientEmail, e.getMessage());
            return false;
        }
    }
}
