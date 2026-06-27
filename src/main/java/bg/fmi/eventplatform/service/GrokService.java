package bg.fmi.eventplatform.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GrokService {

    private final ChatClient chatClient;

    public GrokService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    public String summarizeFeedback(String feedbackText) {
        return chatClient.prompt()
                .user(feedbackText)
                .call()
                .content();
    }
}
