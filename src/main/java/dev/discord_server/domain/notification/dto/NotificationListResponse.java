package dev.discord_server.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record NotificationListResponse(
        @Schema(description = "알림 목록") List<NotificationResponse> notifications,
        @Schema(description = "안 읽은 알림 개수", example = "3") long unreadCount
) {}
