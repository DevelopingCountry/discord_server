package dev.discord_server.domain.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.config.redis.DmSessionTracker;
import dev.discord_server.config.redis.NotificationStreamProducer;
import dev.discord_server.domain.dm_message.dto.DmNotificationPayload;
import dev.discord_server.domain.friend.dto.FriendRequestPayload;
import dev.discord_server.domain.server.dto.InviteNotificationPayload;
import dev.discord_server.domain.server.dto.WebSocketNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {
    private final ObjectMapper objectMapper;
    private final NotificationStreamProducer notificationStreamProducer;
    private final DmSessionTracker dmSessionTracker;

    public void sendInviteNotification(String serverImage, Long toUserId, String serverName, String fromNickname, String fromImageUrl, Long serverId) {
        InviteNotificationPayload payload = new InviteNotificationPayload(
                serverImage,
                serverName,
                fromNickname,
                fromImageUrl,
                "http://localhost:3000/channels/" + serverId
        );

        WebSocketNotification message = new WebSocketNotification("INVITE", payload, toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, message);
        } catch (Exception e) {
            throw new RuntimeException("❌ 초대 알림 스트림 전송 실패", e);
        }
    }

    public void sendFriendRequestNotification(Long toUserId, String fromNickname, String fromImageUrl) {
        FriendRequestPayload payload = new FriendRequestPayload(fromNickname, fromImageUrl);
        WebSocketNotification notification = new WebSocketNotification("FRIEND_REQUEST", payload, toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, notification);
        } catch (Exception e) {
            throw new RuntimeException("❌ 친구 요청 알림 스트림 전송 실패", e);
        }
    }

    public void sendDmNotification(Long dmId, Long toUserId, String fromNickname, String fromImageUrl, String content) {
        if (dmSessionTracker.isUserActiveInDm(dmId.toString(), toUserId)) {
            log.info("📵 DM 채널 접속 중 - 알림 생략: {}", toUserId);
            return;
        }
        DmNotificationPayload payload = new DmNotificationPayload(fromNickname, fromImageUrl, content);
        WebSocketNotification notification = new WebSocketNotification("DM", payload, toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, notification);
        } catch (Exception e) {
            throw new RuntimeException("❌ DM 알림 스트림 전송 실패", e);
        }
    }
}
