package ai.cloudforge.api.ai.core;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EvidenceCollectorTest {

    private EvidenceCollector collector;

    @BeforeEach
    void setUp() {
        collector = new EvidenceCollector();
    }

    @Test
    void testCollectEvidence() {
        List<String> evidence = collector.collectEvidence("LOGS", "NullPointerException at L42", "Runner ping timeout");
        assertEquals(2, evidence.size());
        assertTrue(evidence.get(0).startsWith("[LOGS]"));
    }
}
