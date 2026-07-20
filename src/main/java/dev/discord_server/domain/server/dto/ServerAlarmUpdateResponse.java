package dev.discord_server.domain.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ServerAlarmUpdateResponse {
    @Schema(description = "서버 ID", example = "123456789012345")
    private String serverId;
    @Schema(description = "변경된 알림 수신 여부", example = "true")
    private boolean alarm;
}
