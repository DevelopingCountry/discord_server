package dev.discord_server.auth.dto;

import lombok.Getter;

@Getter
public class RefreshRequestDTO {
    private String refreshToken;
}
