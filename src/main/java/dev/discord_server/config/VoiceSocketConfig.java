package dev.discord_server.config;

import dev.discord_server.auth.util.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
@Slf4j
public class VoiceSocketConfig implements WebSocketConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        log.info("✅ WebSocket 핸들러 등록 - 경로: /ws/voice");

        registry.addHandler(new VoiceSignalingHandler(), "/ws/voice")
                .setAllowedOrigins("*")
                .addInterceptors(new VoiceJwtHandshakeInterceptor(jwtUtil))
                .setHandshakeHandler(new DefaultHandshakeHandler());
    }
}