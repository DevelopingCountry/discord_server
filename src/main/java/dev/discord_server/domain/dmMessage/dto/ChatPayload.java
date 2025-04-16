package dev.discord_server.domain.dmMessage.dto;

public record ChatPayload(
        String dmId,
        String messageId,
        String nickName,
        String imageUrl,
        String content,
        String createdAt
) {}

