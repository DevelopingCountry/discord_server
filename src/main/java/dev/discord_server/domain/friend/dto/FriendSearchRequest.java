package dev.discord_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
//이거 트러블슈팅 왜 no 안쓰면 에러듬??
public class FriendSearchRequest {
    private String nickName;
}
