package dev.discord_server.domain.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelCreatedMessageResponse {
    private String type;
    private String serverId;
    private String channelId;
    private String channelName;


    public ChannelCreatedMessageResponse(String serverId, String channelId, String channelName) {
        this.type = "CREATED";
        this.serverId = serverId;
        this.channelId = channelId;
        this.channelName = channelName;
    }
}
