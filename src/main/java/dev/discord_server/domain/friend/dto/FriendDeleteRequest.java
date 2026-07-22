package dev.discord_server.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendDeleteRequest {
    @Schema(description = "삭제할 상대방 유저 ID", example = "123456789012345")
    private String userId;  //상대 아이디
}
