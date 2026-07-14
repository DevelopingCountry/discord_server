package dev.discord_server.domain.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.redis.DmSessionTracker;
import dev.discord_server.config.redis.NotificationStreamProducer;
import dev.discord_server.domain.dm_message.dto.DmNotificationPayload;
import dev.discord_server.domain.friend.dto.FriendRequestPayload;
import dev.discord_server.domain.friend.dto.FriendResponse;
import dev.discord_server.domain.notification.dto.NotificationListResponse;
import dev.discord_server.domain.notification.dto.NotificationResponse;
import dev.discord_server.domain.notification.entity.Notification;
import dev.discord_server.domain.notification.repository.NotificationRepository;
import dev.discord_server.domain.server.dto.InviteNotificationPayload;
import dev.discord_server.domain.server.dto.WebSocketNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class NotificationService {
    private final ObjectMapper objectMapper;
    private final NotificationStreamProducer notificationStreamProducer;
    private final DmSessionTracker dmSessionTracker;
    private final NotificationRepository notificationRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public void sendInviteNotification(Long inviteId, String serverImage, Long toUserId, String serverName, String fromNickname, String fromImageUrl, Long serverId) {
        InviteNotificationPayload payload = new InviteNotificationPayload(
                inviteId.toString(),
                serverImage,
                serverName,
                fromNickname,
                fromImageUrl,
                "http://localhost:3000/channels/" + serverId
        );

        WebSocketNotification message = new WebSocketNotification("INVITE", payload, toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, message);
            saveNotification(toUserId, "INVITE", payload);
        } catch (Exception e) {
            throw new RuntimeException("❌ 초대 알림 스트림 전송 실패", e);
        }
    }

    public void sendFriendRequestNotification(Long toUserId, String fromNickname, String fromImageUrl) {
        FriendRequestPayload payload = new FriendRequestPayload(fromNickname, fromImageUrl);
        WebSocketNotification notification = new WebSocketNotification("FRIEND_REQUEST", payload, toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, notification);
            saveNotification(toUserId, "FRIEND_REQUEST", payload);
        } catch (Exception e) {
            throw new RuntimeException("❌ 친구 요청 알림 스트림 전송 실패", e);
        }
    }

    public void sendDmNotification(Long dmId, Long toUserId, String fromNickname, String fromImageUrl, String content) {
        if (dmSessionTracker.isUserActiveInDm(dmId.toString(), toUserId)) {
            log.info("📵 DM 채널 접속 중 - 알림 생략: {}", toUserId);
            return;
        }
        DmNotificationPayload payload = new DmNotificationPayload(dmId.toString(), fromNickname, fromImageUrl, content);
        WebSocketNotification notification = new WebSocketNotification("DM", payload, toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, notification);
            saveNotification(toUserId, "DM", payload);
        } catch (Exception e) {
            throw new RuntimeException("❌ DM 알림 스트림 전송 실패", e);
        }
    }

    public void sendFriendOnlineNotification(Long toUserId, FriendResponse friendInfo) {
        WebSocketNotification notification = new WebSocketNotification("FRIEND_ONLINE", friendInfo, toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, notification);
        } catch (Exception e) {
            throw new RuntimeException("❌ 친구 온라인 알림 스트림 전송 실패", e);
        }
    }

    public void sendFriendOfflineNotification(Long toUserId, String friendId) {
        WebSocketNotification notification = new WebSocketNotification("FRIEND_OFFLINE", Map.of("friendId", friendId), toUserId);

        try {
            notificationStreamProducer.publishToUserStream(toUserId, notification);
        } catch (Exception e) {
            throw new RuntimeException("❌ 친구 오프라인 알림 스트림 전송 실패", e);
        }
    }

    private void saveNotification(Long toUserId, String type, Object payload) {
        try {
            Notification notification = Notification.builder()
                    .id(snowflakeIdGenerator.generateId())
                    .userId(toUserId)
                    .type(type)
                    .payload(objectMapper.writeValueAsString(payload))
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("❌ 알림 저장 실패", e);
        }
    }

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long userId) {
        var notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toNotificationResponse)
                .toList();
        long unreadCount = notificationRepository.countByUserIdAndIsReadFalse(userId);
        return new NotificationListResponse(notifications, unreadCount);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsRead(userId);
    }

    private NotificationResponse toNotificationResponse(Notification notification) {
        try {
            Object payload = objectMapper.readValue(notification.getPayload(), Object.class);
            return new NotificationResponse(
                    notification.getId().toString(), notification.getType(), payload, notification.isRead(), notification.getCreatedAt());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("❌ 알림 역직렬화 실패", e);
        }
    }
}
