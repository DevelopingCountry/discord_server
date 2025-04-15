package dev.discord_server.domain.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ServerAlarmUpdateResponse {
    private Long serverId;
    private boolean alarm;
}
