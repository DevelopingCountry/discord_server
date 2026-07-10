package dev.discord_server.domain.server.dto;

public record InviteNotificationPayload(
        Long inviteId,
        String serverImage,
        String serverName,
        String fromNickname,
        String fromImageUrl,
        String serverUrl
) {}
