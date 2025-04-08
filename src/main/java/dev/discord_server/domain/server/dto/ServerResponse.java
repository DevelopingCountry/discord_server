package dev.discord_server.domain.server.dto;

import dev.discord_server.domain.server.entity.Server;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * DTO 이름은 domain이름 + request OR response로 정한다.
 * 각자에 필요한 필드들 작성
 */

@Getter
@Builder
public class ServerResponse {
    private Long id;
    private String name;
    private String image;
    private LocalDateTime createdAt;


    public static ServerResponse toResponseDto(Server server) {
        return ServerResponse.builder()
                .id(server.getId())
                .name(server.getName())
                .image(server.getImage())
                .build();
    }
}
