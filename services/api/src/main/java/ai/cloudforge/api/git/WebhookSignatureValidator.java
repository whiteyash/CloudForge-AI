package ai.cloudforge.api.git;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

@Component
public class WebhookSignatureValidator {

    public boolean validateSignature(String payload, String signature, String secret, String providerName) {
        if (secret == null || signature == null) {
            return false;
        }

        if ("GITLAB".equalsIgnoreCase(providerName)) {
            return secret.equals(signature);
        }

        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmac.init(secretKey);

            byte[] hash = hmac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder("sha256=");
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString().equalsIgnoreCase(signature) || signature.equalsIgnoreCase(hexString.substring(7));
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            return false;
        }
    }
}
