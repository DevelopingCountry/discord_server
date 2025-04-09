package dev.discord_server.domain.friend.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.friend.dto.FriendResponse;
import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;

    public List<FriendResponse> findFriends(UUID currentUserId) {
        List<Friend> friends = friendRepository.findByFromUserIdOrToUserId(currentUserId, currentUserId);

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

    public void changeFriendRequest(UUID uuid, UUID friendId, FriendStatus status) {
        Friend friend = friendRepository
                .findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
                        uuid, friendId,
                        friendId, uuid
                )
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.NOT_VALID_FRIEND));


        // 친구 신청을 받은 사람만 상태값을 변경할 수 있도록 함

        if (!friend.getToUser().getId().equals(uuid)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTH_NOT_CHANGE_FRIEND_STATUS);
        }

        switch (status) {
            case ACCEPTED -> friend.setStatus(FriendStatus.ACCEPTED);
            case REJECTED -> friend.setStatus(FriendStatus.REJECTED);
            default -> throw new NoSuchElementFoundException404(ErrorDefineCode.NOT_VALID_FRIEND_STATUS);
        }

        friendRepository.save(friend);

    }
}
