package ai.cloudforge.api.ai.core;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecommendationFormatterTest {

    private RecommendationFormatter formatter;

    @BeforeEach
    void setUp() {
        formatter = new RecommendationFormatter();
    }

    @Test
    void testFormatRecommendations() {
        List<RecommendationFormatter.FormattedRecommendation> input = List.of(
                new RecommendationFormatter.FormattedRecommendation("Rollback to v1.0", "Container eviction", 90)
        );

        List<String> output = formatter.formatRecommendations(input);
        assertEquals(1, output.size());
        assertTrue(output.get(0).contains("ACTION: Rollback to v1.0"));
    }
}
