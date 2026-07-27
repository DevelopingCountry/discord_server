package dev.discord_server.config;

import dev.discord_server.auth.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   org.springframework.web.socket.WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            String query = servletRequest.getServletRequest().getQueryString();

            if (query != null && query.contains("token=")) {
                String token = query.split("token=")[1];
                try {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    attributes.put("userId", userId);
                    log.info("토큰 인증 성공: userId={}", userId);
                    return true;
                } catch (ExpiredJwtException e) {
                    log.warn("토큰 만료로 핸드셰이크 거부: userId={}", e.getClaims().getSubject());
                } catch (JwtException e) {
                    log.warn("유효하지 않은 토큰으로 핸드셰이크 거부: {}", e.getMessage());
                }
            }
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               org.springframework.web.socket.WebSocketHandler wsHandler,
                               Exception ex) {
    }
}