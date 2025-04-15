package dev.discord_server.domain.dmMessage.dto;

import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
@Getter
public class DmStartRequest {
    private Long targetId;
}