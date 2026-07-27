package dev.discord_server.domain.friend.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.exception.custom.exception.PreconditionFailException412;
import dev.discord_server.domain.friend.dto.FriendAddResponse;

import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.friend.dto.FriendResponse;
import dev.discord_server.domain.friend.dto.FriendStatusResponse;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.server.service.NotificationService;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class FriendService {
    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final NotificationService notificationService;
    private final RedisTemplate<String, String> redisTemplate;

    public List<FriendResponse> findFriends(Long currentUserId) {
        List<Friend> friends = friendRepository.findByFromUserIdOrToUserId(currentUserId, currentUserId);

        return friends.stream()
                .map(friend -> FriendResponse.toFriendResponse(friend, currentUserId))
                .toList();
    }



    public void deleteFriendRequest(Long currentUserId, Long toUserId) {
        User fromUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        User toUser = userRepository.findById(toUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Friend friend = friendRepository
                .findByFromUserAndToUserOrToUserAndFromUser(fromUser, toUser, fromUser, toUser)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_FRIEND));

        // ACCEPTED: 양쪽 모두 삭제 가능
        // PENDING: 요청 보낸 사람(isSender)만 취소 가능

        if (friend.getStatus() == FriendStatus.ACCEPTED) {
            friendRepository.delete(friend);
            notificationService.sendFriendDeleted(currentUserId, toUserId);

            return;
        }
        if (friend.getStatus() == FriendStatus.PENDING) {
            boolean isSender = !friend.getToUser().getId().equals(currentUserId);
            if (!isSender) {
                throw new ForbiddenException403(ErrorDefineCode.AUTH_NOT_CHANGE_FRIEND_STATUS);
            }
            friendRepository.delete(friend);
            return;
        }
        throw new ForbiddenException403(ErrorDefineCode.NOT_DELETABLE_FRIEND_STATUS);
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

        // 상대방 id (= 원래 친구 요청을 보냈던 사람)
        Long targetId = friend.getFromUser().getId().equals(uuid)
                ? friend.getToUser().getId()
                : friend.getFromUser().getId();

        // 원 요청자에게 수락/거절 결과를 실시간으로 알림
        notificationService.sendFriendStatusChangeNotification(
                targetId, FriendResponse.toFriendResponse(friend, targetId));

        notifyFriendsPresenceChange(targetId,true);
        notifyFriendsPresenceChange(uuid,true);

        return new FriendStatusResponse(targetId.toString(),friend.getStatus());
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
        Boolean isSender = relationOpt.map(friend ->
                !friend.getToUser().getId().equals(currentId)
        ).orElse(null);

        FriendResponse response = new FriendResponse(
                targetUser.getId().toString(),
                targetUser.getNickname(),
                targetUser.getImageUrl(),
                status,
                isSender
        );

        return Optional.of(response);

    }

    public List<FriendResponse> findOnlineFriends(Long currentUserId) {
        List<Friend> friends = friendRepository.findByFromUserIdOrToUserId(currentUserId, currentUserId);
        Set<String> onlineUsers = redisTemplate.opsForSet().members("online_users");
        if (onlineUsers == null) return List.of();

        return friends.stream()
                .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                .map(f -> FriendResponse.toFriendResponse(f, currentUserId))
                .filter(f -> onlineUsers.contains(f.getFriendId()))
                .toList();
    }

    /**
     * 유저의 접속/해제를 친구들에게 실시간으로 알림
     * @param userId
     * @param online
     */
    @Transactional(readOnly = true)
    public void notifyFriendsPresenceChange(Long userId, boolean online) {
        friendRepository.findByFromUserIdOrToUserId(userId, userId).stream()
                .filter(f -> f.getStatus() == FriendStatus.ACCEPTED)
                .forEach(f -> {
                    Long targetUserId = f.getFromUser().getId().equals(userId)
                            ? f.getToUser().getId() : f.getFromUser().getId();
                    if (online) {
                        notificationService.sendFriendOnlineNotification(
                                targetUserId, FriendResponse.toFriendResponse(f, targetUserId));
                    } else {
                        notificationService.sendFriendOfflineNotification(targetUserId, userId.toString());
                    }
                });
    }

    public FriendAddResponse sendFriendRequest(Long uuid, String targetNickname) {
        //1. 존재하는가?
        User targetUser = userRepository.findByNickname(targetNickname)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        //2. 나인가?
        User currentUser = userRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        if(currentUser.getNickname().equals(targetNickname)){
            throw new AlreadyExistElementException409(ErrorDefineCode.SELF_FRIEND_REQUEST);
        }
        //3. 이미 친구 상태인가?
        if(friendRepository.existsFriend(currentUser.getId(), targetUser.getId())){
            throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_FRIEND);
        }

        Friend friend = Friend.builder()
                .id(snowflakeIdGenerator.generateId())
                .fromUser(currentUser)
                .toUser(targetUser)
                .status(FriendStatus.PENDING)
                .build();
        friendRepository.save(friend);

        notificationService.sendFriendRequestNotification(targetUser.getId(), currentUser.getNickname(), currentUser.getImageUrl());

        return new FriendAddResponse(targetUser.getId().toString(),targetUser.getNickname(), targetUser.getImageUrl(), FriendStatus.PENDING, true);
    }
}
