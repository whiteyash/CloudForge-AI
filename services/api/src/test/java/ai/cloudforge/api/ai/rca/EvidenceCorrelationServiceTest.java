package ai.cloudforge.api.ai.rca;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EvidenceCorrelationServiceTest {

    private EvidenceCorrelationService service;

    @BeforeEach
    void setUp() {
        service = new EvidenceCorrelationService();
    }

    @Test
    void testCorrelateEvidence() {
        List<String> evidence = service.correlateEvidence("Log error at L42", "Runner ping timeout");
        assertEquals(2, evidence.size());
        assertTrue(evidence.get(0).startsWith("EVIDENCE:"));
    }
}
