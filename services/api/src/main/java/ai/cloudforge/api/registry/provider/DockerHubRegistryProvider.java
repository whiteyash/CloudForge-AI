package ai.cloudforge.api.registry.provider;

import org.springframework.stereotype.Component;

@Component
public class DockerHubRegistryProvider extends AbstractOciRegistryProvider {

    @Override
    public String getRegistryType() {
        return "DOCKER_HUB";
    }
}
