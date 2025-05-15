package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelActionMessage {
    private String id;
    private String name;
    private String type;
    private String creatorId;
    private Long serverId;
    private String action;

    public static ChannelActionMessage from(Channel channel, Long serverId, String action) {
        return new ChannelActionMessage(
                String.valueOf(channel.getId()),
                channel.getName(),
                channel.getType().name(), // enum -> string
                String.valueOf(channel.getCreator().getId()),
                serverId,
                action
        );
    }
}
