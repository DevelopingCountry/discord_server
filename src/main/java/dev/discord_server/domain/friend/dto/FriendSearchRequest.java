package dev.discord_server.domain.friend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
//이거 트러블슈팅 왜 no 안쓰면 에러듬??
public class FriendSearchRequest {
    @Schema(description = "검색할 닉네임", example = "홍길동")
    private String nickName;
}
