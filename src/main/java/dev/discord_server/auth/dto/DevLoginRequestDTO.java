package dev.discord_server.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class DevLoginRequestDTO {
    @Schema(description = "로그인에 사용할 이메일 (실제 존재하지 않아도 됨, 개발용)", example = "dev1@test.local")
    private String email;
    @Schema(description = "로그인에 사용할 닉네임", example = "테스트유저1")
    private String nickname;
}
