package dev.discord_server.auth.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class SecurityUtil {


    public static UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        return UUID.fromString(authentication.getName()); // principal에 userId가 들어있으므로 바로 파싱
    }

}
