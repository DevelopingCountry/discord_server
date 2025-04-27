package dev.discord_server.domain.channel.dto;

public record ChannelWebSocketMessage(
        String type,
        ChannelChatPayload message
) {
}
