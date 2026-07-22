package dev.discord_server.domain.friend.dto;

import dev.discord_server.domain.friend.Enum.FriendStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendAddResponse {
    @Schema(description = "상대방 유저 ID", example = "123456789012345")
    private String friendId;
    @Schema(description = "상대방 닉네임", example = "홍길동")
    private String name;
    @Schema(description = "상대방 프로필 이미지 URL", example = "https://cdn.example.com/profile/123.png")
    private String imageUrl;
    @Schema(description = "친구 상태", example = "PENDING")
    private FriendStatus status;
    @Schema(description = "내가 친구 신청을 보낸 쪽인지 여부", example = "true")
    private Boolean isSender;
}
