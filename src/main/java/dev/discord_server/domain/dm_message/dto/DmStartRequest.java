package dev.discord_server.domain.dm_message.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class DmStartRequest {
    private String targetId;
}