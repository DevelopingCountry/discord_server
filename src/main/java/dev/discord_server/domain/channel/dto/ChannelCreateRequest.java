package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.Enum.ChannelType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ChannelCreateRequest {
    private UUID hostId;
    private String channelName;
    private ChannelType type; // CHAT or VOICE
}
