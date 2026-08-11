package ai.cloudforge.api.ai.operations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SafetyValidationServiceTest {

    private SafetyValidationService service;

    @BeforeEach
    void setUp() {
        service = new SafetyValidationService();
    }

    @Test
    void testValidateRemediationSafety() {
        SafetyValidationService.SafetyCheckResult result = service.validateRemediationSafety(UUID.randomUUID(), "DEPLOYMENT");
        assertNotNull(result);
        assertTrue(result.safeToProceed());
    }
}
