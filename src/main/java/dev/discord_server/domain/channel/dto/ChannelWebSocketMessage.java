package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.message.dto.ChannelChatPayload;

public record ChannelWebSocketMessage(
        String type,
        ChannelChatPayload message
) {
}
