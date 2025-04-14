package dev.discord_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendSearchRequest {
    private String nickName;
}
