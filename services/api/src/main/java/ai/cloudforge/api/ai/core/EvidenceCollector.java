package ai.cloudforge.api.ai.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EvidenceCollector {

    public List<String> collectEvidence(String sourceSystem, String... evidenceItems) {
        List<String> results = new ArrayList<>();
        if (evidenceItems != null) {
            for (String item : evidenceItems) {
                if (item != null && !item.isBlank()) {
                    results.add("[" + sourceSystem + "] " + item);
                }
            }
        }
        return results;
    }
}
