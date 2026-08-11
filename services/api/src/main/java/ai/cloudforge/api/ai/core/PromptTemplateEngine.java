package ai.cloudforge.api.ai.core;

import org.springframework.stereotype.Component;

@Component
public class PromptTemplateEngine {

    public String renderPrompt(String templateName, String rawInput, String contextDetails) {
        return switch (templateName) {
            case "LOG_ANALYSIS" -> "System Prompt: Analyze stack traces and error clusters.\nContext: " + contextDetails + "\nInput: " + rawInput;
            case "ROOT_CAUSE" -> "System Prompt: Perform cross-service causal root cause analysis.\nContext: " + contextDetails + "\nInput: " + rawInput;
            case "PREDICTIVE_RISK" -> "System Prompt: Evaluate pre-flight deployment risk.\nContext: " + contextDetails + "\nInput: " + rawInput;
            default -> "System Prompt: You are CloudForge Mission Control AI Assistant.\nContext: " + contextDetails + "\nInput: " + rawInput;
        };
    }
}
