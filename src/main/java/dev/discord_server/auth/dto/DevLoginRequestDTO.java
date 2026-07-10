package dev.discord_server.auth.dto;

import lombok.Getter;

@Getter
public class DevLoginRequestDTO {
    private String email;
    private String nickname;
}
