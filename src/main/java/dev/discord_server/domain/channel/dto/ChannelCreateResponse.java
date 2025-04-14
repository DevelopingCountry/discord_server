package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.Enum.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;


@Getter
@AllArgsConstructor
public class ChannelCreateResponse {
    private UUID channelId;
    private String channelName;
    private ChannelType channelType;
}
