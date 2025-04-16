package dev.discord_server.domain.server.dto;

import dev.discord_server.domain.server.entity.Server;
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
    private String id;
    private String name;
    private String imageUrl;
    private boolean alarm;
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
