package dev.discord_server.domain.server.dto;

import dev.discord_server.domain.server.entity.Server;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


/**
 * DTO 이름은 domain이름 + request OR response로 정한다.
 * 각자에 필요한 필드들 작성
 */
@Getter
@Builder
@AllArgsConstructor
public class ServerResponse {
    @Schema(description = "서버 ID", example = "123456789012345")
    private String id;
    @Schema(description = "서버 이름", example = "개발자 모임")
    private String name;
    @Schema(description = "서버 이미지 URL", example = "https://cdn.example.com/server/1.png")
    private String imageUrl;
    @Schema(description = "내 알림 수신 여부", example = "true")
    private boolean alarm;
    @Schema(description = "서버 host(생성자)의 유저 ID", example = "123456789012345")
    private String hostId;

    public static ServerResponse toResponseDto(Server server, boolean alarm) {
        return ServerResponse.builder()
                .id(String.valueOf(server.getId()))
                .name(server.getServerName())
                .imageUrl(server.getImage())
                .alarm(alarm)
                .hostId(String.valueOf(server.getHost().getId()))
                .build();
    }
}
