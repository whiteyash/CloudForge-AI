package ai.cloudforge.api.registry.provider;

import java.util.List;

import ai.cloudforge.api.registry.ContainerImageRepositoryDto;
import ai.cloudforge.api.registry.ContainerImageTagDto;
import ai.cloudforge.api.registry.ContainerRegistry;

public interface ContainerRegistryProvider {

    String getRegistryType();

    boolean testConnection(ContainerRegistry registry, String rawCredentials);

    List<ContainerImageRepositoryDto> listRepositories(ContainerRegistry registry, String rawCredentials);

    List<ContainerImageTagDto> listTags(ContainerRegistry registry, String repoId, String repoName, String rawCredentials);

    void deleteTag(ContainerRegistry registry, String repoName, String tagName, String rawCredentials);
}
