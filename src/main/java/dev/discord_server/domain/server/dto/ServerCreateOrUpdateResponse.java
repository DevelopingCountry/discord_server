package dev.discord_server.domain.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerCreateOrUpdateResponse {

    private Long serverId;
    private String imageUrl;
    private String serverName;
}
