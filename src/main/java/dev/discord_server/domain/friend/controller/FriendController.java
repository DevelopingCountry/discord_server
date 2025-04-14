package dev.discord_server.domain.friend.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.friend.dto.*;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friend")
public class FriendController {
    private final FriendService friendService;

    /**
     * 전체 친구 조회
     *
     * @return
     */
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<FriendResponse>> getFriendList() {
        UUID uuid = SecurityUtil.getCurrentUserId();
        List<FriendResponse> friends = friendService.findFriends(uuid);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 친구가 반환되었습니다.", friends);
    }

    /**
     * 친구 추가
     *
     * @param request
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendAddResponse> postFriend(
            @RequestBody FriendAddRequest request) {

        UUID uuid = SecurityUtil.getCurrentUserId();
        FriendAddResponse friendAddResponse = friendService.sendFriendRequest(uuid, request.getTargetId());

        return new CommonResponse<>(true, HttpStatus.OK, "친구 추가 성공했습니다.", friendAddResponse);
    }

    /**
     * 친구 삭제
     *
     * @param request
     * @return
     */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendDeleteResponse> deleteFriend(@RequestBody FriendDeleteRequest request) {
        UUID uuid = SecurityUtil.getCurrentUserId();
        friendService.deleteFriendRequest(uuid, request.getUserId());
        return new CommonResponse<>(true, HttpStatus.OK, "친구 삭제 성공했습니다.", null);
    }

    /**
     * 친구 신청 수락, 거절
     *
     * @param request
     * @return
     */
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendStatusResponse> changeFriendStatus(@RequestBody FriendStatusRequest request) {
        UUID uuid = SecurityUtil.getCurrentUserId();
        FriendStatusResponse friendStatusResponse = friendService.changeFriendRequest(uuid, request.getFriendId(), request.getIsFriend());
        return new CommonResponse<>(true, HttpStatus.OK, "친구 상태 변경에 성공했습니다.", friendStatusResponse);

    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Optional<FriendResponse>> getFriendByNickname(@RequestBody FriendSearchRequest request) {
        UUID uuid = SecurityUtil.getCurrentUserId();
        Optional<FriendResponse> Friend = friendService.findFriendByNickname(uuid, request.getNickName());
        return new CommonResponse<>(true,HttpStatus.OK,"닉네임으로 유저 조회 성공했습니다.",Friend);
    }


}
