package dev.discord_server.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketConnectListener implements ApplicationListener<SessionConnectedEvent> {

    private final NotificationStreamConsumer notificationStreamConsumer;

    @Override
    public void onApplicationEvent(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            Long userId = Long.parseLong(user.getName());
            notificationStreamConsumer.startConsumerForUser(userId);
        }
    }
}
