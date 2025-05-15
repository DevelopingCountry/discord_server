package dev.discord_server.domain.server.dto;

public record WebSocketNotification(
        String action,        // "DM", "INVITE", "FRIEND_REQUEST"
        Object payload,
        Long recipientId    // WebSocket 대상 userId
) {}
