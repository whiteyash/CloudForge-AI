package ai.cloudforge.api.registry.provider;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ContainerRegistryProviderFactory {

    private final Map<String, ContainerRegistryProvider> providers;
    private final GenericOciRegistryProvider fallbackProvider;

    public ContainerRegistryProviderFactory(List<ContainerRegistryProvider> providerList, GenericOciRegistryProvider fallbackProvider) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(
                        p -> p.getRegistryType().toUpperCase(),
                        Function.identity(),
                        (existing, replacement) -> existing
                ));
        this.fallbackProvider = fallbackProvider;
    }

    public ContainerRegistryProvider getProvider(String registryType) {
        if (registryType == null || registryType.isBlank()) {
            return fallbackProvider;
        }
        return providers.getOrDefault(registryType.toUpperCase(), fallbackProvider);
    }
}
