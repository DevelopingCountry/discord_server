package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.domain.dmMessage.dto.WebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisSubscriber implements MessageListener {
    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String payload = new String(message.getBody());
            WebSocketMessage parsed = objectMapper.readValue(payload, WebSocketMessage.class);

            log.info("📩 Redis 수신 및 전송: /topic/dm/{}", parsed.message().dmId());
            template.convertAndSend("/topic/dm/" + parsed.message().dmId(), parsed);
        } catch (Exception e) {
            log.error("❌ Redis 메시지 파싱 실패", e);
        }
    }


}