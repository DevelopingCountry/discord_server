package dev.discord_server.domain.friend.dto;

import dev.discord_server.domain.friend.Enum.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FriendAddResponse {
    private String friendName;
    private String imageUrl;
    private FriendStatus isFriend;
}
