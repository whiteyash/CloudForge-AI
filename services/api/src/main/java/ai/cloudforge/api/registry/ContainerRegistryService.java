package ai.cloudforge.api.registry;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ai.cloudforge.api.auth.ForbiddenException;
import ai.cloudforge.api.auth.RbacService;
import ai.cloudforge.api.auth.ResourceNotFoundException;
import ai.cloudforge.api.project.Project;
import ai.cloudforge.api.project.ProjectRepository;
import ai.cloudforge.api.registry.provider.ContainerRegistryProvider;
import ai.cloudforge.api.registry.provider.ContainerRegistryProviderFactory;

@Service
public class ContainerRegistryService {

    private static final Logger log = LoggerFactory.getLogger(ContainerRegistryService.class);
    private static final String ENCRYPTION_SECRET = "CloudForgeContainerRegistrySecretKey2026";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final ContainerRegistryRepository registryRepository;
    private final ContainerImageRepositoryRepository imageRepositoryRepository;
    private final ContainerImageTagRepository imageTagRepository;
    private final ProjectRepository projectRepository;
    private final RbacService rbacService;
    private final ContainerRegistryProviderFactory providerFactory;

    public ContainerRegistryService(ContainerRegistryRepository registryRepository,
                                    ContainerImageRepositoryRepository imageRepositoryRepository,
                                    ContainerImageTagRepository imageTagRepository,
                                    ProjectRepository projectRepository,
                                    RbacService rbacService,
                                    ContainerRegistryProviderFactory providerFactory) {
        this.registryRepository = registryRepository;
        this.imageRepositoryRepository = imageRepositoryRepository;
        this.imageTagRepository = imageTagRepository;
        this.projectRepository = projectRepository;
        this.rbacService = rbacService;
        this.providerFactory = providerFactory;
    }

    private String getEncryptionSecret() {
        String envKey = System.getenv("CLOUDFORGE_REGISTRY_ENCRYPTION_KEY");
        if (envKey != null && !envKey.isBlank()) {
            return envKey;
        }
        return ENCRYPTION_SECRET;
    }

    @Transactional
    public ContainerRegistryDto connectRegistry(UUID projectId, CreateRegistryRequest request, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, "REGISTRY_MANAGE");

        if (registryRepository.existsByProjectIdAndName(projectId, request.getName())) {
            throw new IllegalArgumentException("Container registry already exists with name: " + request.getName());
        }

        String encryptedSecret = encrypt(request.getCredentials());
        ContainerRegistry registry = new ContainerRegistry(
                projectId,
                orgId,
                request.getName(),
                request.getRegistryType(),
                request.getRegistryUrl(),
                request.getAuthType(),
                encryptedSecret
        );

        // Perform real provider health check GET /v2/
        ContainerRegistryProvider provider = providerFactory.getProvider(request.getRegistryType());
        boolean connected = provider.testConnection(registry, request.getCredentials());
        registry.setStatus(connected ? "CONNECTED" : "UNREACHABLE");

        ContainerRegistry savedRegistry = registryRepository.save(registry);
        log.info("REGISTRY_AUDIT | project={} | user={} | action=REGISTRY_CREATED | details=Connected registry {} type={} status={}",
                projectId, userId, savedRegistry.getName(), savedRegistry.getRegistryType(), savedRegistry.getStatus());

        return new ContainerRegistryDto(savedRegistry);
    }

    @Transactional(readOnly = true)
    public List<ContainerRegistryDto> getRegistriesByProject(UUID projectId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId != null) {
            rbacService.requirePermission(userId, project.getOrganization().getId(), "REGISTRY_VIEW");
        }

        return registryRepository.findByProjectId(projectId).stream()
                .map(ContainerRegistryDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContainerRegistryDto getRegistryById(UUID projectId, UUID id, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId != null) {
            rbacService.requirePermission(userId, project.getOrganization().getId(), "REGISTRY_VIEW");
        }

        ContainerRegistry registry = registryRepository.findByIdAndProjectId(id, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Container registry not found: " + id));
        return new ContainerRegistryDto(registry);
    }

    @Transactional
    public ContainerRegistryDto testConnection(UUID projectId, UUID registryId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, "REGISTRY_MANAGE");

        ContainerRegistry registry = registryRepository.findByIdAndProjectId(registryId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Container registry not found: " + registryId));

        String rawSecret = decrypt(registry.getEncryptedCredentials());
        ContainerRegistryProvider provider = providerFactory.getProvider(registry.getRegistryType());

        boolean ok = provider.testConnection(registry, rawSecret);
        if (ok) {
            registry.setStatus("CONNECTED");
            registryRepository.save(registry);
            return new ContainerRegistryDto(registry);
        } else {
            registry.setStatus("UNREACHABLE");
            registryRepository.save(registry);
            throw new IllegalArgumentException("Container registry connection ping failed: " + registry.getRegistryUrl());
        }
    }

    @Transactional
    public RegistrySyncResultDto syncRegistry(UUID projectId, UUID registryId, UUID userId) {
        long start = System.currentTimeMillis();
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, "REGISTRY_MANAGE");

        ContainerRegistry registry = registryRepository.findByIdAndProjectId(registryId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Container registry not found: " + registryId));

        String rawSecret = decrypt(registry.getEncryptedCredentials());
        ContainerRegistryProvider provider = providerFactory.getProvider(registry.getRegistryType());

        int reposDiscovered = 0;
        int tagsDiscovered = 0;
        int updated = 0;
        int failed = 0;

        try {
            List<ContainerImageRepositoryDto> remoteRepos = provider.listRepositories(registry, rawSecret);
            reposDiscovered = remoteRepos.size();

            for (ContainerImageRepositoryDto repoDto : remoteRepos) {
                String repoName = repoDto.getRepositoryName();
                ContainerImageRepository repo = imageRepositoryRepository.findByRegistryIdAndRepositoryName(registryId, repoName)
                        .orElseGet(() -> imageRepositoryRepository.save(new ContainerImageRepository(registryId, projectId, repoName)));

                List<ContainerImageTagDto> remoteTags = provider.listTags(registry, repo.getId().toString(), repoName, rawSecret);
                tagsDiscovered += remoteTags.size();

                for (ContainerImageTagDto tagDto : remoteTags) {
                    String tagName = tagDto.getTagName();
                    String digest = tagDto.getDigestSha256();
                    imageTagRepository.findByRepositoryIdAndTagName(repo.getId(), tagName)
                            .ifPresentOrElse(existingTag -> {
                                existingTag.setDigestSha256(digest);
                                imageTagRepository.save(existingTag);
                            }, () -> {
                                ContainerImageTag newTag = new ContainerImageTag(
                                        repo.getId(),
                                        tagName,
                                        digest,
                                        tagDto.getSizeBytes() != null ? tagDto.getSizeBytes() : 0L,
                                        tagDto.getArchitecture() != null ? tagDto.getArchitecture() : "linux/amd64",
                                        tagDto.getIsImmutable() != null && tagDto.getIsImmutable()
                                );
                                imageTagRepository.save(newTag);
                            });
                    updated++;
                }

                repo.setImageCount(remoteTags.size());
                imageRepositoryRepository.save(repo);
            }
            long duration = System.currentTimeMillis() - start;
            return new RegistrySyncResultDto(reposDiscovered, tagsDiscovered, updated, failed, duration, "SUCCESS");
        } catch (Exception ex) {
            long duration = System.currentTimeMillis() - start;
            log.warn("REGISTRY_SYNC_FAILED | registry={} | error={}", registryId, ex.getMessage());
            return new RegistrySyncResultDto(reposDiscovered, tagsDiscovered, updated, 1, duration, "FAILED");
        }
    }

    @Transactional
    public void disconnectRegistry(UUID projectId, UUID registryId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, "REGISTRY_MANAGE");

        ContainerRegistry registry = registryRepository.findByIdAndProjectId(registryId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Container registry not found: " + registryId));

        registryRepository.delete(registry);
        log.info("REGISTRY_AUDIT | project={} | user={} | action=REGISTRY_DISCONNECTED | details=Disconnected registry {}",
                projectId, userId, registry.getName());
    }

    @Transactional(readOnly = true)
    public List<ContainerImageRepositoryDto> getImageRepositories(UUID projectId, UUID registryId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId != null) {
            rbacService.requirePermission(userId, project.getOrganization().getId(), "REGISTRY_VIEW");
        }

        return imageRepositoryRepository.findByRegistryId(registryId).stream()
                .map(ContainerImageRepositoryDto::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContainerImageTagDto> getImageTags(UUID projectId, UUID registryId, UUID repositoryId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId != null) {
            rbacService.requirePermission(userId, project.getOrganization().getId(), "REGISTRY_VIEW");
        }

        ContainerImageRepository repo = imageRepositoryRepository.findByIdAndRegistryId(repositoryId, registryId)
                .orElseThrow(() -> new ResourceNotFoundException("Container image repository not found: " + repositoryId));

        String regUrl = registryRepository.findById(registryId).map(ContainerRegistry::getRegistryUrl).orElse("");
        return imageTagRepository.findByRepositoryIdOrderByPushedAtDesc(repositoryId).stream()
                .map(tag -> new ContainerImageTagDto(tag, repo.getRepositoryName(), regUrl))
                .toList();
    }

    @Transactional
    public void deleteImageTag(UUID projectId, UUID registryId, UUID repositoryId, UUID tagId, UUID userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        if (userId == null) {
            throw new ForbiddenException("Unauthenticated request");
        }

        UUID orgId = project.getOrganization().getId();
        rbacService.requirePermission(userId, orgId, "IMAGE_DELETE");

        ContainerImageRepository repo = imageRepositoryRepository.findByIdAndRegistryId(repositoryId, registryId)
                .orElseThrow(() -> new ResourceNotFoundException("Container image repository not found: " + repositoryId));

        ContainerImageTag tag = imageTagRepository.findByIdAndRepositoryId(tagId, repositoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Container image tag not found: " + tagId));

        if (Boolean.TRUE.equals(tag.getIsImmutable())) {
            throw new IllegalArgumentException("Cannot delete immutable image tag: " + tag.getTagName());
        }

        imageTagRepository.delete(tag);

        repo.setImageCount(Math.max(0, repo.getImageCount() - 1));
        imageRepositoryRepository.save(repo);

        log.info("REGISTRY_AUDIT | project={} | user={} | action=IMAGE_TAG_DELETED | details=Deleted image tag {}:{}", projectId, userId, repo.getRepositoryName(), tag.getTagName());
    }

    private String encrypt(String plaintext) {
        if (plaintext == null || plaintext.isBlank()) return null;
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(getEncryptionSecret().getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            byte[] iv = new byte[12];
            SECURE_RANDOM.nextBytes(iv);
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception ex) {
            log.error("Registry credentials encryption failed: {}", ex.getMessage());
            throw new IllegalStateException("Failed to encrypt registry credentials: " + ex.getMessage(), ex);
        }
    }

    private String decrypt(String combinedBase64) {
        if (combinedBase64 == null || combinedBase64.isBlank()) return null;
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256").digest(getEncryptionSecret().getBytes(StandardCharsets.UTF_8));
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
            byte[] combined = Base64.getDecoder().decode(combinedBase64);

            if (combined.length <= 12) {
                return new String(combined, StandardCharsets.UTF_8);
            }

            byte[] iv = new byte[12];
            byte[] ciphertext = new byte[combined.length - 12];
            System.arraycopy(combined, 0, iv, 0, 12);
            System.arraycopy(combined, 12, ciphertext, 0, ciphertext.length);

            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
            byte[] decrypted = cipher.doFinal(ciphertext);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            try {
                return new String(Base64.getDecoder().decode(combinedBase64), StandardCharsets.UTF_8);
            } catch (Exception e) {
                return combinedBase64;
            }
        }
    }
}
