package dev.discord_server.domain.server.dto;

public record InviteNotificationPayload(
        String serverName,
        String fromNickname,
        String fromImageUrl,
        String serverUrl
) {}
