package dev.discord_server.domain.dmMessage.dto;

public record DmNotificationPayload(
        String fromNickname,
        String fromImageUrl,
        String message
) {}
