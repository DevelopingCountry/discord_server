package dev.discord_server.domain.server.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ServerInviteRequest {
    @Schema(description = "초대할 유저 ID", example = "123456789012345")
    private String guestId;
}
