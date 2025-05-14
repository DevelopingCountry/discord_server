package dev.discord_server.domain.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.config.redis.RedisPublisher;
import dev.discord_server.domain.dmMessage.dto.DmNotificationPayload;
import dev.discord_server.domain.friend.dto.FriendRequestPayload;
import dev.discord_server.domain.server.dto.InviteNotificationPayload;
import dev.discord_server.domain.server.dto.WebSocketNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NotificationService {
    private final ObjectMapper objectMapper;
    private final RedisPublisher redisPublisher;

    public void sendInviteNotification(Long toUserId, String serverName, String inviterName, Long inviteId) {
        InviteNotificationPayload payload = new InviteNotificationPayload(
                serverName,
                inviterName,
                "https://yourapp.com/invite/" + inviteId
        );

        WebSocketNotification message = new WebSocketNotification("INVITE", payload, toUserId);

        try {
            String json = objectMapper.writeValueAsString(message);
            redisPublisher.publishNotification(json); // ✅ 통합 알림 채널로 발행
        } catch (Exception e) {
            throw new RuntimeException("❌ 알림 직렬화 실패", e);
        }
    }

    public void sendFriendRequestNotification(Long toUserId, String fromNickname, String fromImageUrl) {
        FriendRequestPayload payload = new FriendRequestPayload(fromNickname, fromImageUrl);

        WebSocketNotification notification = new WebSocketNotification(
                "FRIEND_REQUEST",
                payload,
                toUserId
        );

        try {
            String json = objectMapper.writeValueAsString(notification);
            redisPublisher.publishNotification(json);
        } catch (Exception e) {
            throw new RuntimeException("❌ 친구 요청 알림 직렬화 실패", e);
        }
    }


    public void sendDmNotification(Long toUserId, String senderName, String senderImageUrl, String content) {
        DmNotificationPayload payload = new DmNotificationPayload(senderName, senderImageUrl, content);

        WebSocketNotification notification = new WebSocketNotification("DM", payload, toUserId);

        try {
            String json = objectMapper.writeValueAsString(notification);
            redisPublisher.publishNotification(json);
        } catch (Exception e) {
            throw new RuntimeException("❌ DM 알림 직렬화 실패", e);
        }
    }


}

