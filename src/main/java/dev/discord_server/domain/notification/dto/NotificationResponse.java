package dev.discord_server.domain.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record NotificationResponse(
        @Schema(description = "알림 ID", example = "123456789012345") String id,
        @Schema(description = "알림 타입", example = "SERVER_INVITE") String type,
        @Schema(description = "알림 타입별 상세 데이터") Object payload,
        @Schema(description = "읽음 여부", example = "false") boolean isRead,
        @Schema(description = "알림 생성 시각") LocalDateTime createdAt
) {}
