package dev.discord_server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class VoiceSignalingHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 채널별 세션 목록 (브로드캐스트용)
    private final Map<String, Set<WebSocketSession>> channelSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        log.info("✅ 연결 수립: {}", session.getId());
        // 아무것도 안 함, 메시지 통해 채널 join 예정
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode json = objectMapper.readTree(message.getPayload());
        log.info("📥 메시지 수신: {}", message.getPayload());

        // 필수 필드 확인
        if (!json.has("channelId") || !json.has("type")) {
            log.error("❌ 잘못된 메시지 형식 - channelId 또는 type 필드 누락");
            return;
        }

        String channelId = json.get("channelId").asText();
        String type = json.get("type").asText();

        log.info("메시지 타입: {}, 채널: {}", type, channelId);

        channelSessions.putIfAbsent(channelId, ConcurrentHashMap.newKeySet());

        if ("join".equals(type)) {
            channelSessions.get(channelId).add(session);
            log.info("📥 사용자 입장: channel {}, session {}", channelId, session.getId());

            // 사용자 ID 로깅 (디버깅용)
            if (session.getAttributes().containsKey("userId")) {
                Long userId = (Long) session.getAttributes().get("userId");
                log.info("사용자 ID: {}", userId);
            }
            return;
        }

        if (!channelSessions.containsKey(channelId)) {
            log.warn("⚠️ 존재하지 않는 채널: {}", channelId);
            return;
        }

        // WebRTC 시그널링 메시지 타입 처리 추가
        if ("offer".equals(type) || "answer".equals(type) || "candidate".equals(type)) {
            log.info("WebRTC 시그널링: {}", type);
            // 기존 브로드캐스트 로직 유지
        }

        // 브로드캐스트
        log.info("🔄 브로드캐스트: 채널 {} (사용자 수: {})", channelId, channelSessions.get(channelId).size());
        int sentCount = 0;
        for (WebSocketSession s : channelSessions.get(channelId)) {
            if (s.isOpen() && !s.getId().equals(session.getId())) {
                s.sendMessage(message);
                sentCount++;
            }
        }
        log.info("🔄 브로드캐스트 완료: {} 명에게 전송됨", sentCount);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        for (Set<WebSocketSession> sessions : channelSessions.values()) {
            sessions.remove(session);
        }
        log.info("🚪 연결 종료: {} (상태: {})", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("⚠️ 전송 오류: session={}, 오류={}", session.getId(), exception.getMessage());
    }
}