package dev.discord_server.domain.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerUpdateResponse {
    private Long id;
    private String imageUrl;
    private String serverName;
}
