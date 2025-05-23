package dev.discord_server.domain.friend.dto;

public record FriendRequestPayload(
        String fromNickname,
        String fromImageUrl
) {}
