package dev.discord_server.domain.friend.dto;

import dev.discord_server.domain.friend.Enum.FriendStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendStatusResponse {
    @Schema(description = "상대방 유저 ID", example = "123456789012345")
    private String friendId;
    @Schema(description = "변경된 친구 상태", example = "ACCEPTED")
    private FriendStatus ifFriend;
}
