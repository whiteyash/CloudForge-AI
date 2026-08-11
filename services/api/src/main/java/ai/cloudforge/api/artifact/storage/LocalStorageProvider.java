package ai.cloudforge.api.artifact.storage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class LocalStorageProvider implements ArtifactStorageProvider {

    private final Map<String, byte[]> inMemoryStorage = new ConcurrentHashMap<>();

    @Override
    public String getProviderName() {
        return "LOCAL";
    }

    @Override
    public void storeArtifact(String storageKey, byte[] data) {
        inMemoryStorage.put(storageKey, data);
    }

    @Override
    public byte[] retrieveArtifact(String storageKey) {
        return inMemoryStorage.getOrDefault(storageKey, new byte[0]);
    }

    @Override
    public void deleteArtifact(String storageKey) {
        inMemoryStorage.remove(storageKey);
    }
}
