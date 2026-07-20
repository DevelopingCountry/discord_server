package dev.discord_server.domain.dm_message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class DmStartRequest {
    @Schema(description = "DM을 시작할 상대방 유저 ID", example = "123456789012345")
    private String targetId;
}