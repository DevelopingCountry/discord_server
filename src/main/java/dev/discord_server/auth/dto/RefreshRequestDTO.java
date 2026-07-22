package dev.discord_server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class RefreshRequestDTO {
    @Schema(description = "재발급용 Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
