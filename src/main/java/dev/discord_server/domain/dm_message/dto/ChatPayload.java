package dev.discord_server.domain.dm_message.dto;

public record ChatPayload(
        String dmId,
        String messageId,
        String nickName,
        String imageUrl,
        String content,
        String createdAt,
        String userId
) {}

