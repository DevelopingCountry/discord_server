package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.Enum.ChannelType;
import lombok.Getter;


@Getter
public class ChannelCreateRequest {
    private String channelName;
    private ChannelType type; // CHAT or VOICE
}
