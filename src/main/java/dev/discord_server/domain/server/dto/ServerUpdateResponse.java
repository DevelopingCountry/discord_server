package dev.discord_server.domain.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerUpdateResponse {
    @Schema(description = "서버 ID", example = "123456789012345")
    private String id;
    @Schema(description = "변경된 서버 이미지 URL", example = "https://cdn.example.com/server/1.png")
    private String imageUrl;
    @Schema(description = "변경된 서버 이름", example = "개발자 모임")
    private String serverName;
}
