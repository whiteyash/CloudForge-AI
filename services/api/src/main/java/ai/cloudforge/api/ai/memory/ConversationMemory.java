package ai.cloudforge.api.ai.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class ConversationMemory {

    private final ConcurrentHashMap<UUID, List<String>> shortTermMemory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, List<String>> pinnedLongTermMemory = new ConcurrentHashMap<>();

    public void addShortTermMessage(UUID conversationId, String message) {
        shortTermMemory.computeIfAbsent(conversationId, k -> Collections.synchronizedList(new ArrayList<>())).add(message);
    }

    public List<String> getShortTermHistory(UUID conversationId) {
        return shortTermMemory.getOrDefault(conversationId, List.of());
    }

    public void pinLongTermItem(UUID projectId, String pinnedItem) {
        pinnedLongTermMemory.computeIfAbsent(projectId, k -> Collections.synchronizedList(new ArrayList<>())).add(pinnedItem);
    }

    public List<String> getPinnedLongTermItems(UUID projectId) {
        return pinnedLongTermMemory.getOrDefault(projectId, List.of());
    }
}
