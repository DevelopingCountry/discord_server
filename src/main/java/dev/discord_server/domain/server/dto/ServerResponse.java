package dev.discord_server.domain.server.dto;

import dev.discord_server.domain.server.entity.Server;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO 이름은 domain이름 + request OR response로 정한다.
 * 각자에 필요한 필드들 작성
 */
@Getter
@Builder
public class ServerResponse {
    private UUID id;
    private String name;
    private String image;
    private LocalDateTime createdAt;
    private boolean alarm;

    public static ServerResponse toResponseDto(Server server, boolean alarm) {
        return ServerResponse.builder()
                .id(server.getId())
                .name(server.getName())
                .image(server.getImage())
                .createdAt(server.getCreatedAt())
                .alarm(alarm)
                .build();
    }
}
