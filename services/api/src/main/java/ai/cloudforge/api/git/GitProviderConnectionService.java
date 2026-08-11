package ai.cloudforge.api.git;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.notification.CloudForgeEvent;
import ai.cloudforge.api.notification.EventPublisher;

@Service
public class GitProviderConnectionService {

    private final GitProviderConnectionRepository repository;
    private final GitEncryptionService encryptionService;
    private final EventPublisher eventPublisher;

    public GitProviderConnectionService(
            GitProviderConnectionRepository repository,
            GitEncryptionService encryptionService,
            EventPublisher eventPublisher) {
        this.repository = repository;
        this.encryptionService = encryptionService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<ConnectionResponse> getConnectionsForOrg(UUID orgId) {
        return repository.findByOrgId(orgId).stream()
                .map(ConnectionResponse::fromEntity)
                .toList();
    }

    public String generateAuthUrl(UUID orgId, String providerName) {
        String state = UUID.randomUUID().toString();
        String baseUrl = "https://github.com/login/oauth/authorize";
        if ("GITLAB".equalsIgnoreCase(providerName)) {
            baseUrl = "https://gitlab.com/oauth/authorize";
        } else if ("BITBUCKET".equalsIgnoreCase(providerName)) {
            baseUrl = "https://bitbucket.org/site/oauth2/authorize";
        }
        return baseUrl + "?client_id=cloudforge_app&response_type=code&state=" + state + "&scope=repo,read:org";
    }

    @Transactional
    public ConnectionResponse connectProvider(UUID userId, UUID orgId, String providerName, String accountName, String rawAccessToken, String rawRefreshToken, String scopes) {
        String encAccess = encryptionService.encrypt(rawAccessToken);
        String encRefresh = rawRefreshToken != null ? encryptionService.encrypt(rawRefreshToken) : null;
        String grantedScopes = scopes != null ? scopes : "repo, read:org";

        GitProviderConnection connection = repository.findByOrgIdAndProviderNameAndAccountName(orgId, providerName, accountName)
                .orElseGet(() -> new GitProviderConnection(orgId, providerName, accountName, encAccess, encRefresh, grantedScopes));

        connection.setStatus("ACTIVE");
        connection.setHealthStatus("CONNECTED");
        connection.setLastSyncedAt(Instant.now());
        GitProviderConnection saved = repository.save(connection);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "GIT_PROVIDER_CONNECTED",
                accountName,
                "Git provider " + providerName + " connected for account " + accountName
        ));

        return ConnectionResponse.fromEntity(saved);
    }

    @Transactional
    public ConnectionResponse refreshConnection(UUID userId, UUID orgId, UUID connectionId) {
        GitProviderConnection connection = repository.findById(connectionId)
                .filter(c -> c.getOrgId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Git connection not found"));

        connection.setHealthStatus("CONNECTED");
        connection.setLastSyncedAt(Instant.now());
        GitProviderConnection saved = repository.save(connection);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "TOKEN_REFRESHED",
                connection.getAccountName(),
                "OAuth token refreshed for " + connection.getProviderName() + " (" + connection.getAccountName() + ")"
        ));

        return ConnectionResponse.fromEntity(saved);
    }

    @Transactional
    public void disconnectProvider(UUID userId, UUID orgId, UUID connectionId) {
        GitProviderConnection connection = repository.findById(connectionId)
                .filter(c -> c.getOrgId().equals(orgId))
                .orElseThrow(() -> new ResourceNotFoundException("Git connection not found"));

        repository.delete(connection);

        eventPublisher.publishEvent(new CloudForgeEvent(
                orgId,
                userId,
                "GIT_PROVIDER_DISCONNECTED",
                connection.getAccountName(),
                "Git provider " + connection.getProviderName() + " disconnected for account " + connection.getAccountName()
        ));
    }

    public record ConnectionResponse(
            UUID id,
            UUID orgId,
            String providerName,
            String accountName,
            String status,
            String grantedScopes,
            String healthStatus,
            Integer rateLimitRemaining,
            Instant lastSyncedAt,
            Instant createdAt
    ) {
        public static ConnectionResponse fromEntity(GitProviderConnection c) {
            return new ConnectionResponse(
                    c.getId(),
                    c.getOrgId(),
                    c.getProviderName(),
                    c.getAccountName(),
                    c.getStatus(),
                    c.getGrantedScopes(),
                    c.getHealthStatus(),
                    c.getRateLimitRemaining(),
                    c.getLastSyncedAt(),
                    c.getCreatedAt()
            );
        }
    }
}
