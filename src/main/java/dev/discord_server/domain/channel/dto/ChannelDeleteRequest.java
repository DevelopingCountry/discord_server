package dev.discord_server.domain.channel.dto;

import lombok.Getter;
import java.util.UUID;

@Getter
public class ChannelDeleteRequest {
    private Long channelId;
}
