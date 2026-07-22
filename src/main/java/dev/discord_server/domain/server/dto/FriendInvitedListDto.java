package dev.discord_server.domain.server.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FriendInvitedListDto {
    private String friendId;
    private String name;
    private String imageUrl;
    private boolean invited;
}
