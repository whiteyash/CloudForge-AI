package ai.cloudforge.api.registry.provider;

import org.springframework.stereotype.Component;

@Component
public class EcrRegistryProvider extends AbstractOciRegistryProvider {

    @Override
    public String getRegistryType() {
        return "AWS_ECR";
    }
}
