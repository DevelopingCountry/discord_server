package dev.discord_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FriendDeleteResponse {
    private UUID friendId;

}
