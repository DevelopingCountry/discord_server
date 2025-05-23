package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.domain.channel.dto.ChannelActionMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChannelSubscriber implements MessageListener {
    private final SimpMessagingTemplate template;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            ChannelActionMessage parsed = objectMapper.readValue(message.getBody(), ChannelActionMessage.class);
            Long serverId = parsed.getServerId();
            template.convertAndSend("/topic/server/" + serverId + "/channels", parsed);
        } catch (Exception e) {
            log.error("❌ 채널 생성 메시지 파싱 실패", e);
        }
    }
}
