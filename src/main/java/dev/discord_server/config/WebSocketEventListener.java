package dev.discord_server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String ONLINE_KEY = "online_users";

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getSessionAttributes() == null) return;
        Long userId = (Long) accessor.getSessionAttributes().get("userId");
        if (userId != null) {
            redisTemplate.opsForSet().add(ONLINE_KEY, userId.toString());
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        if (accessor.getSessionAttributes() == null) return;
        Long userId = (Long) accessor.getSessionAttributes().get("userId");
        if (userId != null) {
            redisTemplate.opsForSet().remove(ONLINE_KEY, userId.toString());
        }
    }
}
