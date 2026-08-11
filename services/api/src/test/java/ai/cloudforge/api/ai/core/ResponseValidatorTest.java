package ai.cloudforge.api.ai.core;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ResponseValidatorTest {

    private ResponseValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ResponseValidator();
    }

    @Test
    void testValidResponse() {
        AIResponse<String> response = new AIResponse<>(
                "Summary ok", 85, List.of("Log trace"), "Reasoning ok", List.of("Rollback"), List.of(), List.of(), "Payload"
        );
        assertTrue(validator.isValid(response));
    }

    @Test
    void testInvalidNullSummaryResponse() {
        AIResponse<String> response = new AIResponse<>(
                "", 85, List.of(), "Reasoning ok", List.of(), List.of(), List.of(), "Payload"
        );
        assertFalse(validator.isValid(response));
    }
}
