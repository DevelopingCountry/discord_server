package dev.discord_server.domain.server.dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerImageUpdateRequest {
    private String serverName;
    private String image;
}
