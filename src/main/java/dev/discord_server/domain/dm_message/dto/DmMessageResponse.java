package dev.discord_server.domain.dm_message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DmMessageResponse {
    @Schema(description = "DM 대화 ID", example = "123456789012345")
    private String dmId;
    @Schema(description = "메시지 ID", example = "123456789012345")
    private String messageId;
    @Schema(description = "작성자 유저 ID", example = "123456789012345")
    private String userId;
    @Schema(description = "작성자 닉네임", example = "홍길동")
    private String nickName;
    @Schema(description = "작성자 프로필 이미지 URL", example = "https://cdn.example.com/profile/123.png")
    private String imageUrl;
    @Schema(description = "메시지 내용", example = "안녕하세요")
    private String content;
    @Schema(description = "작성 시각")
    private LocalDateTime createdAt;
}
