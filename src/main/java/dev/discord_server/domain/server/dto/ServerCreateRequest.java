package dev.discord_server.domain.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ServerCreateRequest {
    @Schema(description = "서버 이미지 URL", example = "https://cdn.example.com/server/1.png")
    private String imageUrl;
    @Schema(description = "서버 이름", example = "개발자 모임")
    private String serverName;
}
