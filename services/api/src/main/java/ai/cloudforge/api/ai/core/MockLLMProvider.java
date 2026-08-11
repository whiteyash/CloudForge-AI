package ai.cloudforge.api.ai.core;

import org.springframework.stereotype.Component;

@Component
public class MockLLMProvider implements LLMProvider {

    @Override
    public String getProviderName() {
        return "MockLLMProvider-v1";
    }

    @Override
    public LLMResult generateCompletion(String prompt) {
        String trimmed = prompt != null ? prompt.trim() : "";
        String lower = trimmed.toLowerCase();
        String responseText;

        if (lower.equals("hi") || lower.equals("hii") || lower.equals("hello") || lower.equals("hey") || lower.startsWith("greetings")) {
            responseText = "Hello! I am CloudForge AI Copilot. How can I assist you with your infrastructure, build pipelines, or active incidents today?";
        } else if (lower.contains("802") || lower.contains("incident") || lower.contains("oom") || lower.contains("crash")) {
            responseText = "Incident #INC-802 Analysis: High memory utilization on worker node 'cf-worker-02' triggered OOMKilled container eviction during the build phase. Recommended mitigation: Increase container memory limits to 2Gi and apply Runbook #102.";
        } else if (lower.contains("runner") || lower.contains("scale") || lower.contains("capacity") || lower.contains("pool")) {
            responseText = "Runner Pool Analysis: Current capacity is at 87.5% across 4 active nodes. 1 node is in DRAIN mode for maintenance. Recommending auto-scaling by 2 additional instances.";
        } else if (lower.contains("brief") || lower.contains("daily") || lower.contains("ops") || lower.contains("status")) {
            responseText = "Daily Operations Summary: All 12 production microservices are healthy. 14 deployments executed today with a 99.8% success rate.";
        } else if (lower.contains("why") || lower.contains("failed") || lower.contains("error") || lower.contains("cpu") || lower.contains("memory") || lower.contains("performance")) {
            responseText = "Technical Inquiry Analysis for '" + trimmed + "': System telemetry indicates temporary CPU throttling on cluster node 'prod-k8s-01'. Concurrency limit was reached during the artifact extraction stage.";
        } else {
            responseText = "Analysis complete for query '" + trimmed + "'. CloudForge AI Assistant is ready to help with build pipelines, runner pools, or cluster health.";
        }

        return new LLMResult(responseText, 120, 85, 45L);
    }
}
