package ai.cloudforge.api.ai.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MemoryPolicyTest {

    private MemoryPolicy policy;

    @BeforeEach
    void setUp() {
        policy = new MemoryPolicy();
    }

    @Test
    void testMemoryPolicyLimits() {
        assertEquals(50, policy.getMaxHistoryMessages());
        assertTrue(policy.shouldCleanMemory(55));
    }
}
