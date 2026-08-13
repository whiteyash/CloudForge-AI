package ai.cloudforge.api.ai.core;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.springframework.context.annotation.Primary;

@Component
@Primary
public class RealLLMProvider implements LLMProvider {

    private static final Logger log = LoggerFactory.getLogger(RealLLMProvider.class);
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public RealLLMProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(15))
                .build();
    }

    public boolean isConfigured() {
        String provider = getEnv("AI_PROVIDER", getEnv("LLM_PROVIDER", "gemini"));
        String apiKey = getEnv("GEMINI_API_KEY", getEnv("AI_API_KEY", getEnv("OPENAI_API_KEY", getEnv("ANTHROPIC_API_KEY", ""))));
        return !apiKey.isBlank() || ("custom".equalsIgnoreCase(provider) && !getEnv("AI_BASE_URL", "").isBlank());
    }

    @Override
    public String getProviderName() {
        String provider = getEnv("AI_PROVIDER", getEnv("LLM_PROVIDER", "gemini")).toLowerCase(Locale.ROOT);
        String model = getEnv("GEMINI_MODEL", getEnv("AI_MODEL", getDefaultModel(provider)));
        return "Live-" + provider.toUpperCase(Locale.ROOT) + "-" + model;
    }

    @Override
    public LLMResult generateCompletion(String prompt) {
        if (!isConfigured()) {
            throw new AiProviderNotConfiguredException(
                    "AI Copilot is currently unavailable because no AI provider is configured. " +
                    "Please set GEMINI_API_KEY (or AI_PROVIDER and AI_API_KEY) environment variables."
            );
        }

        String provider = getEnv("AI_PROVIDER", getEnv("LLM_PROVIDER", "gemini")).toLowerCase(Locale.ROOT);
        long startTime = System.currentTimeMillis();

        try {
            if ("anthropic".equals(provider)) {
                return callAnthropicApi(prompt, startTime);
            } else if ("openai".equals(provider)) {
                return callOpenAiCompatibleApi(prompt, startTime);
            } else {
                // Default to Gemini API
                return callGeminiApi(prompt, startTime);
            }
        } catch (AiProviderNotConfiguredException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("AI Provider execution failed: {}", ex.getMessage());
            throw new AiProviderException("AI Provider API call failed: " + ex.getMessage(), ex);
        }
    }

    private LLMResult callOpenAiCompatibleApi(String prompt, long startTime) throws Exception {
        String apiKey = getEnv("AI_API_KEY", getEnv("OPENAI_API_KEY", ""));
        String baseUrl = getEnv("AI_BASE_URL", "https://api.openai.com/v1").replaceAll("/+$", "");
        String model = getEnv("AI_MODEL", "gpt-4o-mini");

        String endpoint = baseUrl + "/chat/completions";

        String jsonPayload = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("model", model)
                .set("messages", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("role", "system")
                                .put("content", "You are CloudForge AI Copilot — an autonomous DevOps, SRE & Cloud Engineering Assistant AND a natural conversational assistant. Answer the user's specific question concisely and accurately based on the provided CloudForge project context. Respond naturally to casual conversation, general technical questions, and operational queries. Do NOT generate Kubernetes YAML unless the user explicitly requests Kubernetes manifests or YAML."))
                        .add(objectMapper.createObjectNode()
                                .put("role", "user")
                                .put("content", prompt))));

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload));

        if (!apiKey.isBlank()) {
            builder.header("Authorization", "Bearer " + apiKey);
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new AiProviderException("OpenAI API returned HTTP " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String textResponse = root.path("choices").get(0).path("message").path("content").asText();
        int promptTokens = root.path("usage").path("prompt_tokens").asInt(100);
        int completionTokens = root.path("usage").path("completion_tokens").asInt(150);
        long latencyMs = System.currentTimeMillis() - startTime;

        return new LLMResult(textResponse, promptTokens, completionTokens, latencyMs);
    }

    private LLMResult callAnthropicApi(String prompt, long startTime) throws Exception {
        String apiKey = getEnv("AI_API_KEY", getEnv("ANTHROPIC_API_KEY", ""));
        String baseUrl = getEnv("AI_BASE_URL", "https://api.anthropic.com/v1").replaceAll("/+$", "");
        String model = getEnv("AI_MODEL", "claude-3-5-sonnet-20241022");

        String endpoint = baseUrl + "/messages";

        String jsonPayload = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("model", model)
                .put("max_tokens", 1024)
                .set("messages", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("role", "user")
                                .put("content", prompt))));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new AiProviderException("Anthropic API returned HTTP " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String textResponse = root.path("content").get(0).path("text").asText();
        int promptTokens = root.path("usage").path("input_tokens").asInt(100);
        int completionTokens = root.path("usage").path("output_tokens").asInt(150);
        long latencyMs = System.currentTimeMillis() - startTime;

        return new LLMResult(textResponse, promptTokens, completionTokens, latencyMs);
    }

    private LLMResult callGeminiApi(String prompt, long startTime) throws Exception {
        String apiKey = getEnv("GEMINI_API_KEY", getEnv("AI_API_KEY", ""));
        String model = getEnv("GEMINI_MODEL", getEnv("AI_MODEL", "gemini-1.5-pro"));
        String baseUrl = getEnv("AI_BASE_URL", "https://generativelanguage.googleapis.com/v1beta/models").replaceAll("/+$", "");

        String endpoint = baseUrl + "/" + model + ":generateContent";

        ObjectNode systemInstructionNode = objectMapper.createObjectNode();
        systemInstructionNode.set("parts", objectMapper.createArrayNode()
                .add(objectMapper.createObjectNode()
                        .put("text", "You are CloudForge AI Copilot — an autonomous DevOps, SRE & Cloud Engineering Assistant AND a natural conversational assistant. Answer the user's specific question concisely and accurately based on the provided CloudForge project context. Respond naturally to casual conversation, general technical questions, and operational queries. Do NOT generate Kubernetes YAML unless the user explicitly requests Kubernetes manifests or YAML.")));

        ObjectNode contentNode = objectMapper.createObjectNode();
        contentNode.put("role", "user");
        contentNode.set("parts", objectMapper.createArrayNode()
                .add(objectMapper.createObjectNode()
                        .put("text", prompt)));

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.set("system_instruction", systemInstructionNode);
        rootNode.set("contents", objectMapper.createArrayNode().add(contentNode));

        String jsonPayload = objectMapper.writeValueAsString(rootNode);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload));

        if (!apiKey.isBlank()) {
            builder.header("x-goog-api-key", apiKey);
        }

        HttpResponse<String> response = httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new AiProviderException("Gemini API returned HTTP " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String textResponse = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
        long latencyMs = System.currentTimeMillis() - startTime;

        return new LLMResult(textResponse, 120, 150, latencyMs);
    }

    private String getEnv(String name, String defaultValue) {
        String val = System.getenv(name);
        return (val != null && !val.isBlank()) ? val.trim() : defaultValue;
    }

    private String getDefaultModel(String provider) {
        return switch (provider) {
            case "anthropic" -> "claude-3-5-sonnet-20241022";
            case "gemini" -> "gemini-1.5-pro";
            default -> "gpt-4o-mini";
        };
    }
}
