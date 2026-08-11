package ai.cloudforge.api.registry.provider;

import org.springframework.stereotype.Component;

@Component
public class GhcrRegistryProvider extends AbstractOciRegistryProvider {

    @Override
    public String getRegistryType() {
        return "GITHUB_GHCR";
    }
}
