package dev.discord_server.domain.channel.dto;

public record ChannelChatPayload(
        String channelId,
        String messageId,
        String senderNickname,
        String senderImageUrl,
        String content,
        String createdAt
) {
}
