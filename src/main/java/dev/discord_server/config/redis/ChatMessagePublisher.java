package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.config.redis.dto.ChatMessagePayload;
import dev.discord_server.config.redis.dto.ChatSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatMessagePublisher {
    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void publishChannelMessage(String type, ChatMessagePayload payload) {
        publish(type, payload, redisPublisher::publishMessage);
    }

    public void publishDmMessage(String type, ChatMessagePayload payload) {
        publish(type, payload, redisPublisher::publishDm);
    }

    private void publish(String type, ChatMessagePayload payload, java.util.function.Consumer<String> sender) {
        try {
            log.info("📤 Redis 전송 [{}] - messageId: {}, content: {}", type, payload.messageId(), payload.content());
            String json = objectMapper.writeValueAsString(new ChatSocketMessage(type, payload));
            sender.accept(json);
        } catch (Exception e) {
            log.error("❌ Redis 메시지 전송 실패", e);
        }
    }
}
