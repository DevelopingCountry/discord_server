package dev.discord_server.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
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
        String userId = json.has("userId") ? json.get("userId").asText() : null;

        log.info("메시지 타입: {}, 채널: {}, 사용자: {}", type, channelId, userId);

        channelSessions.putIfAbsent(channelId, ConcurrentHashMap.newKeySet());

        if ("join".equals(type)) {
            Set<WebSocketSession> channelParticipants = channelSessions.get(channelId);

            // 🔥 핵심 수정: 새 사용자 입장 시 상호 알림 (자기 자신 제외)

            // 1. 기존 사용자들에게 새 사용자 입장 알림 (자기 자신 제외)
            log.info("📤 기존 사용자들에게 새 사용자 입장 알림: userId={}, 채널={}", userId, channelId);
            for (WebSocketSession existingSession : channelParticipants) {
                if (existingSession.isOpen() && !existingSession.getId().equals(session.getId())) {
                    try {
                        Map<String, Object> userJoinedMessage = new HashMap<>();
                        userJoinedMessage.put("type", "user-joined");
                        userJoinedMessage.put("userId", userId);
                        userJoinedMessage.put("channelId", channelId);

                        String messageJson = objectMapper.writeValueAsString(userJoinedMessage);
                        existingSession.sendMessage(new TextMessage(messageJson));
                        log.info("📤 기존 사용자에게 알림 전송: session={}", existingSession.getId());
                    } catch (Exception e) {
                        log.error("❌ 기존 사용자에게 알림 전송 실패: {}", e.getMessage());
                    }
                }
            }

            // 2. 새 사용자에게 기존 사용자들 알림 (자기 자신 제외)
            log.info("📤 새 사용자에게 기존 사용자들 알림: 기존 사용자 수={}", channelParticipants.size());
            for (WebSocketSession existingSession : channelParticipants) {
                if (existingSession.isOpen() && !existingSession.getId().equals(session.getId())) {
                    try {
                        // 기존 사용자의 userId 가져오기
                        Object existingUserId = existingSession.getAttributes().get("userId");
                        if (existingUserId != null && !existingUserId.toString().equals(userId)) {
                            Map<String, Object> existingUserMessage = new HashMap<>();
                            existingUserMessage.put("type", "user-joined");
                            existingUserMessage.put("userId", existingUserId.toString());
                            existingUserMessage.put("channelId", channelId);

                            String messageJson = objectMapper.writeValueAsString(existingUserMessage);
                            session.sendMessage(new TextMessage(messageJson));
                            log.info("📤 새 사용자에게 기존 사용자 알림: existingUserId={}", existingUserId);
                        }
                    } catch (Exception e) {
                        log.error("❌ 새 사용자에게 기존 사용자 알림 실패: {}", e.getMessage());
                    }
                }
            }

            // 3. 새 사용자를 채널에 추가
            channelParticipants.add(session);
            log.info("📥 사용자 입장 완료: channel={}, session={}, 총 참가자 수={}",
                    channelId, session.getId(), channelParticipants.size());

            // 사용자 ID 로깅 (디버깅용)
            if (session.getAttributes().containsKey("userId")) {
                Long sessionUserId = (Long) session.getAttributes().get("userId");
                log.info("세션 사용자 ID: {}", sessionUserId);
            }
            return;
        }

        if ("leave".equals(type)) {
            Set<WebSocketSession> channelParticipants = channelSessions.get(channelId);
            if (channelParticipants != null) {
                channelParticipants.remove(session);

                // 다른 사용자들에게 나간 사용자 알림 (자기 자신 제외)
                log.info("📤 사용자 나감 알림: userId={}, 채널={}", userId, channelId);
                for (WebSocketSession remainingSession : channelParticipants) {
                    if (remainingSession.isOpen() && !remainingSession.getId().equals(session.getId())) {
                        try {
                            Object remainingUserId = remainingSession.getAttributes().get("userId");
                            // 나가는 사용자와 다른 사용자에게만 알림
                            if (remainingUserId != null && !remainingUserId.toString().equals(userId)) {
                                Map<String, Object> userLeftMessage = new HashMap<>();
                                userLeftMessage.put("type", "user-left");
                                userLeftMessage.put("userId", userId);
                                userLeftMessage.put("channelId", channelId);

                                String messageJson = objectMapper.writeValueAsString(userLeftMessage);
                                remainingSession.sendMessage(new TextMessage(messageJson));
                                log.info("📤 사용자 나감 알림 전송: session={}", remainingSession.getId());
                            }
                        } catch (Exception e) {
                            log.error("❌ 사용자 나감 알림 전송 실패: {}", e.getMessage());
                        }
                    }
                }

                log.info("📤 사용자 나감 완료: channel={}, 남은 참가자 수={}", channelId, channelParticipants.size());
            }
            return;
        }

        if (!channelSessions.containsKey(channelId)) {
            log.warn("⚠️ 존재하지 않는 채널: {}", channelId);
            return;
        }

        // WebRTC 시그널링 메시지 처리
        if ("offer".equals(type) || "answer".equals(type) || "ice-candidate".equals(type)) {
            log.info("🔄 WebRTC 시그널링 처리: {}", type);

            // targetUserId가 있는 경우 특정 사용자에게만 전송
            if (json.has("targetUserId")) {
                String targetUserId = json.get("targetUserId").asText();
                log.info("🎯 특정 사용자에게 시그널링 전송: targetUserId={}, fromUserId={}", targetUserId, userId);

                // 자기 자신에게는 전송하지 않음
                if (targetUserId.equals(userId)) {
                    log.warn("⚠️ 자기 자신에게 시그널링 전송 시도 차단: userId={}", userId);
                    return;
                }

                Set<WebSocketSession> channelParticipants = channelSessions.get(channelId);
                for (WebSocketSession targetSession : channelParticipants) {
                    if (targetSession.isOpen() && !targetSession.getId().equals(session.getId())) {
                        Object sessionUserId = targetSession.getAttributes().get("userId");
                        if (sessionUserId != null && sessionUserId.toString().equals(targetUserId)) {
                            try {
                                // 원본 메시지에 fromUserId 추가
                                Map<String, Object> forwardMessage = new HashMap<>();
                                forwardMessage.put("type", type);
                                forwardMessage.put("userId", userId); // 보낸 사람의 userId
                                forwardMessage.put("channelId", channelId);
                                forwardMessage.put("targetUserId", targetUserId);

                                if (json.has("offer")) {
                                    forwardMessage.put("offer", json.get("offer"));
                                }
                                if (json.has("answer")) {
                                    forwardMessage.put("answer", json.get("answer"));
                                }
                                if (json.has("candidate")) {
                                    forwardMessage.put("candidate", json.get("candidate"));
                                }

                                String messageJson = objectMapper.writeValueAsString(forwardMessage);
                                targetSession.sendMessage(new TextMessage(messageJson));
                                log.info("🎯 시그널링 전송 완료: targetUserId={}", targetUserId);
                                return;
                            } catch (Exception e) {
                                log.error("❌ 시그널링 전송 실패: targetUserId={}, 오류={}", targetUserId, e.getMessage());
                            }
                        }
                    }
                }
                log.warn("⚠️ 대상 사용자를 찾을 수 없음: targetUserId={}", targetUserId);
                return;
            }
        }

        // 일반 브로드캐스트 (모든 사용자에게, 자기 자신 제외)
        log.info("🔄 브로드캐스트: 채널 {} (사용자 수: {})", channelId, channelSessions.get(channelId).size());
        int sentCount = 0;
        for (WebSocketSession s : channelSessions.get(channelId)) {
            if (s.isOpen() && !s.getId().equals(session.getId())) {
                try {
                    s.sendMessage(message);
                    sentCount++;
                } catch (Exception e) {
                    log.error("❌ 브로드캐스트 전송 실패: session={}, 오류={}", s.getId(), e.getMessage());
                }
            }
        }
        log.info("🔄 브로드캐스트 완료: {} 명에게 전송됨", sentCount);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 연결이 끊어진 세션을 모든 채널에서 제거하고 알림
        Object userId = session.getAttributes().get("userId");

        for (Map.Entry<String, Set<WebSocketSession>> entry : channelSessions.entrySet()) {
            String channelId = entry.getKey();
            Set<WebSocketSession> sessions = entry.getValue();

            if (sessions.remove(session)) {
                log.info("🚪 채널에서 세션 제거: channelId={}, sessionId={}", channelId, session.getId());

                // 다른 사용자들에게 나간 사용자 알림 (자기 자신 제외)
                if (userId != null) {
                    for (WebSocketSession remainingSession : sessions) {
                        if (remainingSession.isOpen() && !remainingSession.getId().equals(session.getId())) {
                            try {
                                Object remainingUserId = remainingSession.getAttributes().get("userId");
                                // 나가는 사용자와 다른 사용자에게만 알림
                                if (remainingUserId != null && !remainingUserId.toString().equals(userId.toString())) {
                                    Map<String, Object> userLeftMessage = new HashMap<>();
                                    userLeftMessage.put("type", "user-left");
                                    userLeftMessage.put("userId", userId.toString());
                                    userLeftMessage.put("channelId", channelId);

                                    String messageJson = objectMapper.writeValueAsString(userLeftMessage);
                                    remainingSession.sendMessage(new TextMessage(messageJson));
                                    log.info("📤 연결 종료 시 사용자 나감 알림: userId={}", userId);
                                }
                            } catch (Exception e) {
                                log.error("❌ 연결 종료 시 알림 실패: {}", e.getMessage());
                            }
                        }
                    }
                }
                break; // 한 채널에서만 제거되면 됨
            }
        }

        log.info("🚪 연결 종료: {} (상태: {})", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("⚠️ 전송 오류: session={}, 오류={}", session.getId(), exception.getMessage());
    }
}