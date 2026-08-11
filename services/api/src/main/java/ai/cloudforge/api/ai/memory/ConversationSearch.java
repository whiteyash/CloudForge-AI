package ai.cloudforge.api.ai.memory;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ConversationSearch {

    public List<String> searchMemory(List<String> memoryItems, String keyword) {
        if (memoryItems == null || keyword == null || keyword.isBlank()) {
            return List.of();
        }
        String lower = keyword.toLowerCase();
        return memoryItems.stream()
                .filter(item -> item != null && item.toLowerCase().contains(lower))
                .toList();
    }
}
