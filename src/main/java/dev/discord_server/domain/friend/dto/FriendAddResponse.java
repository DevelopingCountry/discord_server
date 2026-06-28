package dev.discord_server.domain.friend.dto;

import dev.discord_server.domain.friend.Enum.FriendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendAddResponse {
    private String friendId;
    private String name;
    private String imageUrl;
    private FriendStatus status;
    private Boolean isSender;
}
