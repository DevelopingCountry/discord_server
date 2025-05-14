package dev.discord_server.domain.dmMessage.dto;

public record DmNotificationPayload(
        String senderName,
        String senderImageUrl,
        String message
) {}
