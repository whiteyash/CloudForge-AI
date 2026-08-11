package ai.cloudforge.api.registry;

import java.time.Instant;
import java.util.UUID;

public class ContainerImageTagDto {

    private UUID id;
    private UUID repositoryId;
    private String tagName;
    private String digestSha256;
    private Long sizeBytes;
    private String architecture;
    private Boolean isImmutable;
    private Instant pushedAt;
    private String pullCommand;

    public ContainerImageTagDto() {
    }

    public ContainerImageTagDto(ContainerImageTag tag, String repositoryName, String registryUrl) {
        this.id = tag.getId();
        this.repositoryId = tag.getRepositoryId();
        this.tagName = tag.getTagName();
        this.digestSha256 = tag.getDigestSha256();
        this.sizeBytes = tag.getSizeBytes();
        this.architecture = tag.getArchitecture();
        this.isImmutable = tag.getIsImmutable();
        this.pushedAt = tag.getPushedAt();

        String host = (registryUrl != null) ? registryUrl.replaceAll("^https?://", "").replaceAll("/+$", "") : "registry.cloudforge.ai";
        this.pullCommand = "docker pull " + host + "/" + repositoryName + ":" + tag.getTagName();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(UUID repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getDigestSha256() {
        return digestSha256;
    }

    public void setDigestSha256(String digestSha256) {
        this.digestSha256 = digestSha256;
    }

    public Long getSizeBytes() {
        return sizeBytes;
    }

    public void setSizeBytes(Long sizeBytes) {
        this.sizeBytes = sizeBytes;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public Boolean getIsImmutable() {
        return isImmutable;
    }

    public void setIsImmutable(Boolean isImmutable) {
        this.isImmutable = isImmutable;
    }

    public Instant getPushedAt() {
        return pushedAt;
    }

    public void setPushedAt(Instant pushedAt) {
        this.pushedAt = pushedAt;
    }

    public String getPullCommand() {
        return pullCommand;
    }

    public void setPullCommand(String pullCommand) {
        this.pullCommand = pullCommand;
    }
}
