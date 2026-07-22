package dev.discord_server.config;

import dev.discord_server.domain.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final RedisTemplate<String, String> redisTemplate;
    private final FriendService friendService;
    private static final String ONLINE_KEY = "online_users";

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            redisTemplate.opsForSet().add(ONLINE_KEY, user.getName()); // 접속 시(로그인) redis에 저장
            friendService.notifyFriendsPresenceChange(Long.parseLong(user.getName()), true); // 친구들에게 접속했다고 알림
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Principal user = event.getUser();
        if (user != null) {
            redisTemplate.opsForSet().remove(ONLINE_KEY, user.getName()); // !접속 시(로그아웃) redis에서 삭제
            friendService.notifyFriendsPresenceChange(Long.parseLong(user.getName()), false); // 친구들에게 로그아웃 했다고 알림
        }
    }
}
