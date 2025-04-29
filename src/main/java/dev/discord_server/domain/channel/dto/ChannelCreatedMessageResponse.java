package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelCreatedMessageResponse {
    private String id;
    private String name;
    private String type;
    private String creatorId;
    private Long serverId;

    public static ChannelCreatedMessageResponse from(Channel channel, Long serverId) {
        return new ChannelCreatedMessageResponse(
                String.valueOf(channel.getId()),
                channel.getName(),
                channel.getType().name(), // enum -> string
                String.valueOf(channel.getCreator().getId()),
                serverId
        );
    }
}
