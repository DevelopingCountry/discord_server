package dev.discord_server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNicknameRequest {
    @Schema(description = "변경할 닉네임", example = "새로운닉네임")
    private String nickname;
}
