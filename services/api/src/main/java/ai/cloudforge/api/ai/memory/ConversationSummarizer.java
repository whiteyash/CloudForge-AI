package ai.cloudforge.api.ai.memory;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ConversationSummarizer {

    public String summarizeHistory(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return "No previous conversation history.";
        }
        return "Conversation contains " + messages.size() + " turns. Key topic focus: operational analysis & root cause evaluation.";
    }
}
