package dev.discord_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendDeleteRequest {
    private UUID userId;  //상대 아이디
}
