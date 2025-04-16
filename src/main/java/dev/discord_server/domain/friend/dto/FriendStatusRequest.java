package dev.discord_server.domain.friend.dto;

import dev.discord_server.domain.friend.Enum.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendStatusRequest {
    private String friendId;
    private FriendStatus isFriend;
}
