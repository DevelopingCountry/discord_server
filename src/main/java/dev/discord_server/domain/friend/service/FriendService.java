package dev.discord_server.domain.friend.service;

import dev.discord_server.domain.dto.FriendResponse;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public List<FriendResponse> findFriends(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Friend> friends = friendRepository.findByFromUserOrToUser(currentUser, currentUser);

        return friends.stream()
                .map(friend -> FriendResponse.toFriendResponse(friend, currentUserId))
                .toList();
    }
}
