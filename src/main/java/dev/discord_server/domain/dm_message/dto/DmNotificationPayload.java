package dev.discord_server.domain.dm_message.dto;

public record DmNotificationPayload(
        String dmId,
        String fromNickname,
        String fromImageUrl,
        String message
) {}
