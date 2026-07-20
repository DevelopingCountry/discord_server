package dev.discord_server.domain.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ServerAlarmUpdateRequest {
    @Schema(description = "알림 수신 여부", example = "true")
    private boolean alarm;
}
