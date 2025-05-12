package dev.discord_server.domain.dm_message.dto;

public record WebSocketMessage(
        String type,
        ChatPayload message
) {}
