package ai.cloudforge.api.shared;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/smtp")
public class SmtpController {

    private final EmailService emailService;

    public SmtpController(EmailService emailService) {
        this.emailService = emailService;
    }

    public record SmtpConfigRequest(
            String host,
            int port,
            String username,
            String password,
            String fromAddress,
            boolean useTls,
            boolean enabled
    ) {}

    public record SmtpTestRequest(
            String host,
            int port,
            String username,
            String password,
            boolean useTls
    ) {}

    public record SendTestEmailRequest(
            String recipientEmail,
            String subject,
            String content
    ) {}

    @GetMapping("/config")
    public ResponseEntity<?> getSmtpStatus() {
        return ResponseEntity.ok(java.util.Map.of(
                "configured", emailService.isConfigured()
        ));
    }

    @PostMapping("/config")
    public ResponseEntity<?> updateSmtpConfig(@RequestBody SmtpConfigRequest req) {
        emailService.updateSmtpConfig(
                req.host(),
                req.port() <= 0 ? 587 : req.port(),
                req.username(),
                req.password(),
                req.fromAddress(),
                req.useTls(),
                req.enabled()
        );
        return ResponseEntity.ok(java.util.Map.of("message", "SMTP Configuration updated successfully"));
    }

    @PostMapping("/test")
    public ResponseEntity<EmailService.SmtpTestResult> testSmtpConnection(@RequestBody SmtpTestRequest req) {
        EmailService.SmtpTestResult result = emailService.testConnection(
                req.host(),
                req.port() <= 0 ? 587 : req.port(),
                req.username(),
                req.password(),
                req.useTls()
        );
        return ResponseEntity.ok(result);
    }
}
