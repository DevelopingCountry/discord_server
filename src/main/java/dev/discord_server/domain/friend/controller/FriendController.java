package dev.discord_server.domain.friend.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.friend.dto.*;
import dev.discord_server.domain.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class FriendController {
    private final FriendService friendService;

    @GetMapping("/friend")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<FriendResponse>> getFriendList() {
        UUID uuid = SecurityUtil.getCurrentUserId();
        List<FriendResponse> friends = friendService.findFriends(uuid);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 친구가 반환되었습니다.",friends);
    }
    @PostMapping("/friend")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendAddResponse> postFriend(
            @RequestBody FriendAddRequest request) {

        UUID uuid = SecurityUtil.getCurrentUserId();
        friendService.sendFriendRequest(uuid,request.getUserId());
        return new CommonResponse<>(true,HttpStatus.OK,"친구 추가 성공했습니다.",null);
    }

    /**
     * 친구 삭제
     * @param request
     * @return
     */
    @DeleteMapping("/friend")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendDeleteResponse> deleteFriend(@RequestBody FriendDeleteRequest request) {
        UUID uuid = SecurityUtil.getCurrentUserId();
        friendService.deleteFriendRequest(uuid,request.getUserId());
        return new CommonResponse<>(true,HttpStatus.OK,"친구 삭제 성공했습니다.",null);
    }



}
