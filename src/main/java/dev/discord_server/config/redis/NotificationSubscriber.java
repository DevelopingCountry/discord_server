package dev.discord_server.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.domain.server.dto.WebSocketNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            WebSocketNotification notification = objectMapper.readValue(json, WebSocketNotification.class);

            messagingTemplate.convertAndSendToUser(
                    notification.recipientId().toString(),
                    "/queue/notifications",
                    notification
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
