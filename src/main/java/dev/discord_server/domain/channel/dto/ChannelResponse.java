package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelResponse {
    private String id;
    private String name;
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
