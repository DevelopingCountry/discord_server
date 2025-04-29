package dev.discord_server.domain.friend.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.exception.custom.exception.PreconditionFailException412;
import dev.discord_server.domain.friend.dto.FriendAddResponse;
import dev.discord_server.domain.friend.dto.FriendResponse;
import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.friend.dto.FriendStatusResponse;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public List<FriendResponse> findFriends(Long currentUserId) {
        List<Friend> friends = friendRepository.findDistinctFriendsByUserId(currentUserId);

        Map<Long, Friend> uniqueFriends = new HashMap<>();

        for (Friend friend : friends) {
            // 상대방 유저 ID 결정
            Long targetUserId = friend.getFromUser().getId().equals(currentUserId)
                    ? friend.getToUser().getId()
                    : friend.getFromUser().getId();

            // 이미 해당 유저 관계가 있으면, ACCEPTED 상태를 우선시
            if (uniqueFriends.containsKey(targetUserId)) {
                Friend existingFriend = uniqueFriends.get(targetUserId);
                // ACCEPTED 상태가 있으면 그것을 유지
                if (existingFriend.getStatus() != FriendStatus.ACCEPTED &&
                        friend.getStatus() == FriendStatus.ACCEPTED) {
                    uniqueFriends.put(targetUserId, friend);
                }
            } else {
                uniqueFriends.put(targetUserId, friend);
            }
        }

        return uniqueFriends.values().stream()
                .map(friend -> FriendResponse.toFriendResponse(friend, currentUserId.toString()))
                .toList();
    }

    /**
     * 친구 추가
     * @param currentUserId
     * @param toUserId
     * @return
     */
    public FriendAddResponse sendFriendRequest(Long currentUserId, Long toUserId) {
        User fromUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        if (currentUserId.equals(toUserId)) {
            throw new AlreadyExistElementException409(ErrorDefineCode.SELF_FRIEND_REQUEST);
        }

        Optional<Friend> existingFriendOpt = friendRepository.findByFromUserAndToUserOrToUserAndFromUser(
                fromUser, toUser, toUser, fromUser);

        if (existingFriendOpt.isPresent()) {
            Friend friend = existingFriendOpt.get();
            switch (friend.getStatus()) {
                case ACCEPTED -> throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_FRIEND);
                case PENDING -> {
                    if (friend.getToUser().getId().equals(currentUserId)) {
                        friend.setStatus(FriendStatus.ACCEPTED);
                        friendRepository.save(friend);
                        return new FriendAddResponse(toUser.getId().toString(), toUser.getNickname(),
                                toUser.getImageUrl(), FriendStatus.ACCEPTED);
                    } else {
                        throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_FRIEND);
                    }
                }
                case REJECTED -> {
                    friend.setStatus(FriendStatus.PENDING);
                    friendRepository.save(friend);
                    // 거절된 요청을 다시 요청할 때, 업데이트된 상태로 응답 반환
                    return new FriendAddResponse(toUser.getId().toString(), toUser.getNickname(),
                            toUser.getImageUrl(), FriendStatus.PENDING);
                }
            }
        }

        Friend friend = Friend.builder()
                .id(snowflakeIdGenerator.generateId())
                .fromUser(fromUser)
                .toUser(toUser)
                .status(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend);

        return new FriendAddResponse(toUser.getId().toString(), toUser.getNickname(),
                toUser.getImageUrl(), FriendStatus.PENDING);
    }

    public void deleteFriendRequest(Long currentUserId, Long toUserId) {
        User fromUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Friend friend = friendRepository
                .findByFromUserAndToUserOrToUserAndFromUser(fromUser, toUser, toUser, fromUser)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_FRIEND));

        // 수락된 친구 관계는 누구나 삭제 가능
        // PENDING 상태는 친구 요청을 보낸 사람만 취소 가능
        if (friend.getStatus() == FriendStatus.PENDING && !friend.getFromUser().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.NOT_DELETABLE_FRIEND_STATUS);
        }
        // REJECTED 상태는 삭제 불가
        if (friend.getStatus() == FriendStatus.REJECTED) {
            throw new ForbiddenException403(ErrorDefineCode.NOT_DELETABLE_FRIEND_STATUS);
        }

        friendRepository.delete(friend);
    }

    /**
     * 친구 수락, 거절
     * @param uuid
     * @param friendId
     * @param status
     * @return
     */
    public FriendStatusResponse changeFriendRequest(Long uuid, Long friendId, FriendStatus status) {
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
            default -> throw new PreconditionFailException412(ErrorDefineCode.NOT_VALID_FRIEND_STATUS);
        }

        friendRepository.save(friend);

        // 항상 친구 요청을 보낸 사람의 ID를 반환 (수정된 부분)
        Long targetId = friend.getFromUser().getId();

        return new FriendStatusResponse(targetId.toString(), friend.getStatus());
    }

    /**
     * 특정 인물 한명 닉네임으로 조회하기
     * @param currentId
     * @param nickname
     * @return
     */
    public Optional<FriendResponse> findFriendByNickname(Long currentId,String nickname){
        Optional<User> targetOpt = userRepository.findByNickname(nickname);
        if (targetOpt.isEmpty() || targetOpt.get().getId().equals(currentId)) {
            return Optional.empty();
        }
        User targetUser = targetOpt.get();
        User currentUser = userRepository.findById(currentId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Optional<Friend> relationOpt = friendRepository.findByFromUserAndToUserOrToUserAndFromUser(
                currentUser, targetUser, currentUser, targetUser
        );
        FriendStatus status = relationOpt.map(Friend::getStatus).orElse(null);

        FriendResponse response = new FriendResponse(
                targetUser.getId().toString(),
                targetUser.getNickname(),
                targetUser.getImageUrl(),
                status
        );

        return Optional.of(response);

    }
}
