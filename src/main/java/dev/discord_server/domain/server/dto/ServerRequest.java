package dev.discord_server.domain.server.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class ServerRequest {
    private String serverName;
    private String image;
}
