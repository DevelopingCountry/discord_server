package dev.discord_server.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendDeleteResponse {
    @Schema(description = "삭제된 상대방 유저 ID", example = "123456789012345")
    private String friendId;

}
