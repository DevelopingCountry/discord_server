package dev.discord_server.domain.channel.dto;

import lombok.Getter;

@Getter
public class ChannelUpdateRequest {
    private Long channelId;
    private String channelName;
}