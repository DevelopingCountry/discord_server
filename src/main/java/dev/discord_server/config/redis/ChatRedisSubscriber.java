package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.config.redis.dto.ChatSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatRedisSubscriber implements MessageListener {
    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String redisTopic = new String(message.getChannel());
        String destinationPrefix = redisTopic.equals("chat.dm") ? "/topic/dm/" : "/topic/channel/";

        try {
            ChatSocketMessage parsed = objectMapper.readValue(message.getBody(), ChatSocketMessage.class);
            template.convertAndSend(destinationPrefix + parsed.message().targetId(), parsed);
        } catch (Exception e) {
            log.error("❌ 채팅 메시지 파싱 실패", e);
        }
    }
}
