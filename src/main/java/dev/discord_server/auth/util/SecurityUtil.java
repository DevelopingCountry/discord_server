package dev.discord_server.auth.util;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public class SecurityUtil {


    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName()); // principal에 userId가 들어있으므로 바로 파싱
    }

    public static Long getCurrentUserId(Message<?> message) {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        Object userId = accessor.getSessionAttributes().get("userId");

        if (userId instanceof Long) {
            return (Long) userId;
        }

        throw new RuntimeException("WebSocket 인증 정보가 없습니다.");
    }
}
