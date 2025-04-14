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
    private UUID serverId;
    private String name;
    private String imageUrl;
    private boolean alarm;
    private UUID hostId;

    public static ServerResponse toResponseDto(Server server, boolean alarm) {
        return ServerResponse.builder()
                .serverId(server.getId())
                .name(server.getServerName())
                .imageUrl(server.getImage())
                .alarm(alarm)
                .hostId(server.getHost().getId())
                .build();
    }
}
