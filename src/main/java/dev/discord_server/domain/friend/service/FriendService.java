package dev.discord_server.domain.friend.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.friend.dto.FriendResponse;
import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public List<FriendResponse> findFriends(UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        List<Friend> friends = friendRepository.findByFromUserOrToUser(currentUser, currentUser);

        return friends.stream()
                .map(friend -> FriendResponse.toFriendResponse(friend, currentUserId))
                .toList();
    }

    public void sendFriendRequest(UUID currentUserId, UUID toUserId) {
        User fromUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        boolean exists = friendRepository.existsByFromUserAndToUser(fromUser, toUser);

        // custom 에러처리로 수정
        if (exists) {
            throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_FRIEND);
        }

        if (currentUserId.equals(toUserId)) {
            throw new AlreadyExistElementException409(ErrorDefineCode.SELF_FRIEND_REQUEST);
        }

        Friend friend = Friend.builder()
                .fromUser(fromUser)
                .toUser(toUser)
                .status(FriendStatus.PENDING)
                .build();

        friendRepository.save(friend);
    }


    public void deleteFriendRequest(UUID currentUserId, UUID toUserId) {
        User fromUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Friend friend = friendRepository
                .findByFromUserAndToUserOrToUserAndFromUser(fromUser, toUser, toUser, fromUser)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_FRIEND));

        friendRepository.delete(friend);
    }
}
