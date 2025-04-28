package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.domain.channel.dto.ChannelWebSocketMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class MessageRedisSubscriber implements MessageListener {
    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate template;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChannelWebSocketMessage parsed = objectMapper.readValue(message.getBody(), ChannelWebSocketMessage.class);
            template.convertAndSend("/topic/channel/" + parsed.message().channelId(),parsed);
        } catch (Exception e) {
            log.error("channel Message 파싱 실패함");
            throw new RuntimeException(e);
        }
    }
}
