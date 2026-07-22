package dev.discord_server.domain.friend.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.friend.dto.*;
import dev.discord_server.domain.friend.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "친구 API", description = "친구 목록/신청/수락/거절/삭제, 닉네임 검색, 온라인 상태 조회 API")
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
    @Operation(summary = "전체 친구 목록 조회", description = "로그인한 유저의 전체 친구 목록을 반환합니다.")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<FriendResponse>> getFriendList() {
        Long uuid = SecurityUtil.getCurrentUserId();
        List<FriendResponse> friends = friendService.findFriends(uuid);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 친구가 반환되었습니다.", friends);
    }

    /**
     * 친구 추가
     *
     * @param request
     * @return
     */
    @Operation(summary = "친구 신청", description = "다른 유저에게 친구 신청을 보냅니다. 상태는 PENDING으로 생성됩니다.")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendAddResponse> postFriend(
            @RequestBody FriendAddRequest request) {

        Long uuid = SecurityUtil.getCurrentUserId();
        FriendAddResponse friendAddResponse = friendService.sendFriendRequest(uuid, Long.valueOf(request.getTargetId()));

        return new CommonResponse<>(true, HttpStatus.OK, "친구 추가 성공했습니다.", friendAddResponse);
    }

    /**
     * 친구 삭제
     *
     * @param request
     * @return
     */
    @Operation(summary = "친구 삭제", description = "친구 관계를 삭제합니다.")
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendDeleteResponse> deleteFriend(@RequestBody FriendDeleteRequest request) {
        Long uuid = SecurityUtil.getCurrentUserId();
        friendService.deleteFriendRequest(uuid, Long.valueOf(request.getUserId()));
        return new CommonResponse<>(true, HttpStatus.OK, "친구 삭제 성공했습니다.", null);
    }

    /**
     * 친구 신청 수락, 거절
     *
     * @param request
     * @return
     */
    @Operation(summary = "친구 신청 수락/거절", description = "받은 친구 신청을 ACCEPTED 또는 REJECTED 상태로 변경합니다.")
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<FriendStatusResponse> changeFriendStatus(@RequestBody FriendStatusRequest request) {
        Long uuid = SecurityUtil.getCurrentUserId();
        FriendStatusResponse friendStatusResponse = friendService.changeFriendRequest(uuid, Long.valueOf(request.getFriendId()), request.getIsFriend());
        return new CommonResponse<>(true, HttpStatus.OK, "친구 상태 변경에 성공했습니다.", friendStatusResponse);

    }

    @Operation(summary = "닉네임으로 유저 검색", description = "닉네임으로 유저를 검색해 친구 신청 대상 정보를 반환합니다.")
    @PostMapping("/search")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Optional<FriendResponse>> getFriendByNickname(@RequestBody FriendSearchRequest request) {
        Long uuid = SecurityUtil.getCurrentUserId();
        Optional<FriendResponse> friend = friendService.findFriendByNickname(uuid, request.getNickName());
        return new CommonResponse<>(true,HttpStatus.OK,"닉네임으로 유저 조회 성공했습니다.",friend);
    }


    @Operation(summary = "온라인 친구 목록 조회", description = "현재 접속 중인 친구 목록을 반환합니다.")
    @GetMapping("/online")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<FriendResponse>> getOnlineFriends() {
        Long uuid = SecurityUtil.getCurrentUserId();
        List<FriendResponse> friends = friendService.findOnlineFriends(uuid);
        return new CommonResponse<>(true, HttpStatus.OK, "온라인 친구 목록입니다.", friends);
    }

}
