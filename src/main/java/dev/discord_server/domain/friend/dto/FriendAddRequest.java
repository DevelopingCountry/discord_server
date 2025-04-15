package dev.discord_server.domain.friend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendAddRequest {
    private Long targetId;

}
