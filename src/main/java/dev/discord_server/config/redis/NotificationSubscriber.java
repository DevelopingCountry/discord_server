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

            System.out.println("🔴 전송되는 알림 JSON: " + json);

            //recipientId.toString()은 내부적으로 Principal.getName()과 매칭
            //principal은 userId로 설정이 되어있기때문에
            //다음과같이 해석
            /*
            convertAndSendToUser("123", "/queue/notifications", ...)
            → "/user/123/queue/notifications" 로 내부 처리
            → 유저 123이 구독한 "/user/queue/notifications" 에게 도달
             */
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
