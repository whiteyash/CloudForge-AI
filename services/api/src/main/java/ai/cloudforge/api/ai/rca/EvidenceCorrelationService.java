package ai.cloudforge.api.ai.rca;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class EvidenceCorrelationService {

    public List<String> correlateEvidence(String... evidenceSources) {
        List<String> correlated = new ArrayList<>();
        if (evidenceSources != null) {
            for (String src : evidenceSources) {
                if (src != null && !src.isBlank()) {
                    correlated.add("EVIDENCE: " + src);
                }
            }
        }
        return correlated;
    }
}
