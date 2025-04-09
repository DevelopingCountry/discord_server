package dev.discord_server.domain.friend.dto;

import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class FriendResponse {
    private UUID friendId;
    private String name;
    private String imageUrl;

    public static FriendResponse toFriendResponse(Friend friend, UUID currentUserId) {
        User targetUser = friend.getFromUser().getId().equals(currentUserId)
                ? friend.getToUser()
                : friend.getFromUser();

        return FriendResponse.builder()
                .friendId(friend.getId())
                .name(targetUser.getNickname())
                .imageUrl(targetUser.getImageUrl())
                .build();
    }
}
