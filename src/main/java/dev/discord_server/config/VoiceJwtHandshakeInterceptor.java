package dev.discord_server.config;

import dev.discord_server.auth.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class VoiceJwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = servletRequest.getParameter("token");

        log.info("🔑 WebSocket 연결 시도, 토큰 존재 여부: {}", (token != null ? "있음" : "없음"));

        if (token == null || !isValid(token)) {
            log.error("❌ 토큰 검증 실패: 토큰이 없거나 유효하지 않음");
            return false;
        }

        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            String email = jwtUtil.getEmailFromToken(token); // 필요 시

            log.info("✅ 토큰 검증 성공 - 사용자 ID: {}, 이메일: {}", userId, email);

            attributes.put("userId", userId);
            attributes.put("email", email);
            return true;
        } catch (Exception e) {
            log.error("❌ 토큰에서 정보 추출 중 오류: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            log.error("⚠️ 핸드셰이크 후 예외 발생: {}", exception.getMessage());
        }
    }

    private boolean isValid(String token) {
        try {
            jwtUtil.getUserIdFromToken(token); // 파싱 성공 여부로 유효성 판단
            return true;
        } catch (Exception e) {
            log.error("❌ 토큰 유효성 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}