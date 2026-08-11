package ai.cloudforge.api.ai.log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.stereotype.Service;

@Service
public class ExceptionFingerprintService {

    public String generateFingerprintHash(String exceptionClass, String failedMethod, String failedFile, int lineNumber) {
        String raw = exceptionClass + ":" + failedMethod + ":" + failedFile + ":" + lineNumber;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "HASH-" + Math.abs(raw.hashCode());
        }
    }
}
