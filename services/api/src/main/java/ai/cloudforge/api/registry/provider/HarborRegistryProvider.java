package ai.cloudforge.api.registry.provider;

import org.springframework.stereotype.Component;

@Component
public class HarborRegistryProvider extends AbstractOciRegistryProvider {

    @Override
    public String getRegistryType() {
        return "HARBOR";
    }
}
