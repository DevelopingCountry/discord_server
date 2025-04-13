package dev.discord_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendAddRequest {
    private UUID targetId;

}
