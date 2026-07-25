package dev.discord_server.config.redis.dto;

public record ChatMessagePayload(
        String targetId,
        String messageId,
        String nickName,
        String imageUrl,
        String content,
        String createdAt,
        String userId
) {}
