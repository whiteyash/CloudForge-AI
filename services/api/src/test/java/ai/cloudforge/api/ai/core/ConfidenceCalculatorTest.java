package ai.cloudforge.api.ai.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConfidenceCalculatorTest {

    private ConfidenceCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new ConfidenceCalculator();
    }

    @Test
    void testCalculateConfidenceWithEvidenceBonus() {
        int confidence = calculator.calculateConfidence(0.85, 3, true);
        assertEquals(95, confidence);
    }
}
