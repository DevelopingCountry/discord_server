package dev.discord_server.domain.message.dto;

public record ChannelWebSocketMessage(
        String type,
        ChannelChatPayload message
) {}