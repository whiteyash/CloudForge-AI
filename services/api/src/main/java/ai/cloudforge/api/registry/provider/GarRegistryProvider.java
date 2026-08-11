package ai.cloudforge.api.registry.provider;

import org.springframework.stereotype.Component;

@Component
public class GarRegistryProvider extends AbstractOciRegistryProvider {

    @Override
    public String getRegistryType() {
        return "GOOGLE_GAR";
    }
}
