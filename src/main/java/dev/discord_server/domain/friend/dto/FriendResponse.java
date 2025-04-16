package dev.discord_server.domain.friend.dto;

import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class FriendResponse {
    private String friendId;
    private String name;
    private String imageUrl;
    private FriendStatus status;

    public static FriendResponse toFriendResponse(Friend friend, String currentUserId) {
        User targetUser = friend.getFromUser().getId().toString().equals(currentUserId)
                ? friend.getToUser()
                : friend.getFromUser();

        return FriendResponse.builder()
                .friendId(friend.getId().toString())
                .name(targetUser.getNickname())
                .imageUrl(targetUser.getImageUrl())
                .status(friend.getStatus())
                .build();
    }
}
