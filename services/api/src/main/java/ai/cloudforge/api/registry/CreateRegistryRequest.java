package ai.cloudforge.api.registry;

import jakarta.validation.constraints.NotBlank;

public class CreateRegistryRequest {

    @NotBlank(message = "Registry name is required")
    private String name;

    @NotBlank(message = "Registry type is required")
    private String registryType; // DOCKER_HUB, AWS_ECR, GOOGLE_GAR, GITHUB_GHCR, AZURE_ACR, HARBOR_PRIVATE

    @NotBlank(message = "Registry URL is required")
    private String registryUrl;

    @NotBlank(message = "Auth type is required")
    private String authType; // TOKEN, USERNAME_PASSWORD, AWS_IAM, SERVICE_ACCOUNT

    private String credentials; // Plaintext token or username:password; will be encrypted at rest

    public CreateRegistryRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getRegistryUrl() {
        return registryUrl;
    }

    public void setRegistryUrl(String registryUrl) {
        this.registryUrl = registryUrl;
    }

    public String getAuthType() {
        return authType;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials;
    }
}
