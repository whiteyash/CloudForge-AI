package ai.cloudforge.api.registry.provider;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.cloudforge.api.registry.ContainerImageRepositoryDto;
import ai.cloudforge.api.registry.ContainerImageTagDto;
import ai.cloudforge.api.registry.ContainerRegistry;

public abstract class AbstractOciRegistryProvider implements ContainerRegistryProvider {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean testConnection(ContainerRegistry registry, String rawCredentials) {
        String registryUrl = registry.getRegistryUrl();
        if (registryUrl == null || registryUrl.isBlank()) {
            return false;
        }
        try {
            String pingUrl = registryUrl.replaceAll("/+$", "") + "/v2/";
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(4))
                    .build();

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(pingUrl))
                    .timeout(Duration.ofSeconds(4))
                    .GET();

            applyAuthHeader(builder, rawCredentials);

            HttpResponse<String> response = client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            int code = response.statusCode();
            return (code == 200 || code == 204 || code == 401);
        } catch (Exception ex) {
            log.warn("[{}] Registry connection test failed for {}: {}", getRegistryType(), registryUrl, ex.getMessage());
            return false;
        }
    }

    @Override
    public List<ContainerImageRepositoryDto> listRepositories(ContainerRegistry registry, String rawCredentials) {
        return Collections.emptyList();
    }

    @Override
    public List<ContainerImageTagDto> listTags(ContainerRegistry registry, String repoId, String repoName, String rawCredentials) {
        return Collections.emptyList();
    }

    @Override
    public void deleteTag(ContainerRegistry registry, String repoName, String tagName, String rawCredentials) {
        throw new UnsupportedOperationException("Remote tag deletion is not supported by provider " + getRegistryType() + " through the standard V2 REST API.");
    }

    protected void applyAuthHeader(HttpRequest.Builder builder, String rawCredentials) {
        if (rawCredentials != null && !rawCredentials.isBlank()) {
            if (rawCredentials.contains(":")) {
                String basic = Base64.getEncoder().encodeToString(rawCredentials.getBytes(StandardCharsets.UTF_8));
                builder.header("Authorization", "Basic " + basic);
            } else {
                builder.header("Authorization", "Bearer " + rawCredentials);
            }
        }
    }
}
