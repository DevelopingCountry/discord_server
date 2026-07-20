package dev.discord_server.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendAddRequest {
    @Schema(description = "친구 신청 대상 유저 ID", example = "123456789012345")
    private String targetId;

}
