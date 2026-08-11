package ai.cloudforge.api.registry.provider;

import org.springframework.stereotype.Component;

@Component
public class AcrRegistryProvider extends AbstractOciRegistryProvider {

    @Override
    public String getRegistryType() {
        return "AZURE_ACR";
    }
}
