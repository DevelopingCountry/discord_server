package dev.discord_server.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final DmSessionTracker dmSessionTracker;

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String dmId = (String) accessor.getSessionAttributes().get("dmId");
        Long userId = (Long) accessor.getSessionAttributes().get("userId");

        if (dmId != null && userId != null) {
            dmSessionTracker.leaveDm(dmId, userId);
        }
    }
}