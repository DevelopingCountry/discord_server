package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelResponse {
    private String channelId;
    private String channelName;
    private String type;
    private String creatorId;

    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
                String.valueOf(channel.getId()),
                channel.getName(),
                channel.getType().name(), // enum -> string
                String.valueOf(channel.getCreator().getId())
        );
    }
}
