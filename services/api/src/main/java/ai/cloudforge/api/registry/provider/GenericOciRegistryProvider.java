package ai.cloudforge.api.registry.provider;

import org.springframework.stereotype.Component;

@Component
public class GenericOciRegistryProvider extends AbstractOciRegistryProvider {

    @Override
    public String getRegistryType() {
        return "GENERIC_OCI";
    }
}
