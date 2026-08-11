package ai.cloudforge.api.registry;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TriggerBuildRequest {

    @NotNull(message = "Target registry ID is required")
    private UUID registryId;

    @NotBlank(message = "Repository name is required")
    private String repositoryName;

    @NotBlank(message = "Tag name is required")
    private String tagName;

    private String dockerfilePath = "Dockerfile";

    public TriggerBuildRequest() {
    }

    public UUID getRegistryId() {
        return registryId;
    }

    public void setRegistryId(UUID registryId) {
        this.registryId = registryId;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDockerfilePath() {
        return dockerfilePath;
    }

    public void setDockerfilePath(String dockerfilePath) {
        this.dockerfilePath = dockerfilePath;
    }
}
