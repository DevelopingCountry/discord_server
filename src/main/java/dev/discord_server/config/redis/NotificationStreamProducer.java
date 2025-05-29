package dev.discord_server.config.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.domain.server.dto.WebSocketNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationStreamProducer {

    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;

    public void publishToUserStream(Long userId, WebSocketNotification notification) {
        try {
            String key = "notifications:" + userId;
            Map<String, String> fields = Map.of(
                    "action", notification.action(),
                    "payload", objectMapper.writeValueAsString(notification.payload())
            );
            redisTemplate.opsForStream().add(key, fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("❌ 알림 직렬화 실패", e);
        }
    }
}
