package dev.discord_server.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class UserResponse {
    @Schema(description = "유저 ID", example = "123456789012345")
    private String id;
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;
    @Schema(description = "프로필 이미지 URL", example = "https://cdn.example.com/profile/123.png")
    private String imageUrl;


}
