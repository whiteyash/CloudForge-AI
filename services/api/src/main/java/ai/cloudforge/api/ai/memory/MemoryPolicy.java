package ai.cloudforge.api.ai.memory;

import org.springframework.stereotype.Component;

@Component
public class MemoryPolicy {

    private final int maxHistoryMessages = 50;
    private final int retentionHours = 24;

    public int getMaxHistoryMessages() {
        return maxHistoryMessages;
    }

    public int getRetentionHours() {
        return retentionHours;
    }

    public boolean shouldCleanMemory(int currentHistorySize) {
        return currentHistorySize > maxHistoryMessages;
    }
}
