package ai.cloudforge.api.registry;

public class RegistrySyncResultDto {

    private int repositoriesDiscovered;
    private int tagsDiscovered;
    private int updated;
    private int failed;
    private long durationMs;
    private String status;

    public RegistrySyncResultDto() {
    }

    public RegistrySyncResultDto(int repositoriesDiscovered, int tagsDiscovered, int updated, int failed, long durationMs, String status) {
        this.repositoriesDiscovered = repositoriesDiscovered;
        this.tagsDiscovered = tagsDiscovered;
        this.updated = updated;
        this.failed = failed;
        this.durationMs = durationMs;
        this.status = status;
    }

    public int getRepositoriesDiscovered() {
        return repositoriesDiscovered;
    }

    public void setRepositoriesDiscovered(int repositoriesDiscovered) {
        this.repositoriesDiscovered = repositoriesDiscovered;
    }

    public int getTagsDiscovered() {
        return tagsDiscovered;
    }

    public void setTagsDiscovered(int tagsDiscovered) {
        this.tagsDiscovered = tagsDiscovered;
    }

    public int getUpdated() {
        return updated;
    }

    public void setUpdated(int updated) {
        this.updated = updated;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
