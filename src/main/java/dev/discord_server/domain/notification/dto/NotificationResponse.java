package dev.discord_server.domain.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        String id,
        String type,
        Object payload,
        boolean isRead,
        LocalDateTime createdAt
) {}
