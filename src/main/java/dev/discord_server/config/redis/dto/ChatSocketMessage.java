package dev.discord_server.config.redis.dto;

public record ChatSocketMessage(
        String type,
        ChatMessagePayload message
) {}
