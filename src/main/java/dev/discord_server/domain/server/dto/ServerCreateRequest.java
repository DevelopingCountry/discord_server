package dev.discord_server.domain.server.dto;

import lombok.Getter;

@Getter
public class ServerCreateRequest {
    private String imageUrl;
    private String serverName;
}
