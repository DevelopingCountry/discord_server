package dev.discord_server.domain.message.dto;

public record ChannelChatPayload(
        String channelId,
        String messageId,
        String nickName,
        String imageUrl,
        String content,
        String createdAt
) {}
