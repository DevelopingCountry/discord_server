package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChannelResponse {
    private UUID channelId;
    private String channelName;
    private String type;
    private UUID creatorId;

    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getType().name(), // enum -> string
                channel.getCreator().getId()
        );
    }
}
