package dev.discord_server.domain.friend.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class FriendDeleteRequest {
    private UUID userId;  //상대 아이디
}
