package dev.discord_server.domain.dmMessage.dto;

public record WebSocketMessage(
        String type,
        ChatPayload message
) {}
