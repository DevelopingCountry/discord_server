package dev.discord_server.domain.dm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DmUserResponse {
    @Schema(description = "DM 대화 ID", example = "123456789012345")
    private String dmId;
    @Schema(description = "상대방 유저 ID", example = "123456789012345")
    private String targetId;
    @Schema(description = "상대방 프로필 이미지 URL", example = "https://cdn.example.com/profile/123.png")
    private String targetImageUrl;
    @Schema(description = "상대방 닉네임", example = "홍길동")
    private String targetNickname;
    @Schema(description = "안읽은 메시지 개수", example = "3")
    private long unreadCount;
}
