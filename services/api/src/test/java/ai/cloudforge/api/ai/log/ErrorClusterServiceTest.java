package ai.cloudforge.api.ai.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ErrorClusterServiceTest {

    private ErrorClusterService clusterService;

    @BeforeEach
    void setUp() {
        clusterService = new ErrorClusterService();
    }

    @Test
    void testClassifyErrorCluster() {
        assertEquals("NullPointerException", clusterService.classifyErrorCluster("java.lang.NullPointerException", ""));
        assertEquals("TimeoutException", clusterService.classifyErrorCluster("Connection timed out", ""));
        assertEquals("DatabaseError", clusterService.classifyErrorCluster("PostgreSQL SQL error", ""));
    }
}
