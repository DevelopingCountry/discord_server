package dev.discord_server.domain.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerUpdateResponse {
    private String id;
    private String imageUrl;
    private String serverName;
}
