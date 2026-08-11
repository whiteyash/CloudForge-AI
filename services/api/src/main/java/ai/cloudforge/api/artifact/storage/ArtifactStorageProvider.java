package ai.cloudforge.api.artifact.storage;

public interface ArtifactStorageProvider {

    String getProviderName();

    void storeArtifact(String storageKey, byte[] data);

    byte[] retrieveArtifact(String storageKey);

    void deleteArtifact(String storageKey);
}
