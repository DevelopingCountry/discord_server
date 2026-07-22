package dev.discord_server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    @Schema(description = "Access Token (유효기간 3시간)", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;
    @Schema(description = "Refresh Token (유효기간 7일)", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
    @Schema(description = "유저 ID", example = "123456789012345")
    private String userId;
}
