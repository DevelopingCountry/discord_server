package dev.discord_server.domain.server.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.server.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import dev.discord_server.domain.server.service.ServerService;

import java.util.List;


/**
 * 반환타입 CommonResponse
 * CommonResponse 안에는 파라미터로 4가지가 필요
 * 성공여부, HTTP Status, 응답 메시지, 응답데이터
 *
 */
@Slf4j
@Tag(name = "서버 API", description = "서버(길드) 생성/조회/수정/삭제, 초대, 알림 설정 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/server")
public class ServerController {
    private final ServerService serverService;

    @Operation(summary = "내 서버 목록 조회", description = "로그인한 유저가 속한 서버 목록을 반환합니다.")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<ServerResponse>> getServerList() {
        List<ServerResponse> servers = serverService.findServers();
        return new CommonResponse<>(true, HttpStatus.OK, "모든 서버가 반환되었습니다.",servers);
    }


    @Operation(summary = "서버 생성", description = "새 서버(길드)를 생성합니다. 생성한 유저가 host가 됩니다.")
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ServerResponse> addServer(@RequestBody ServerCreateRequest serverCreateRequest) {
        ServerResponse serverResponse = serverService.addServer(serverCreateRequest);
        return new CommonResponse<>(true, HttpStatus.OK, "서버가 생성되었습니다.", serverResponse);
    }

    @Operation(summary = "서버 정보 수정", description = "서버 이름/이미지를 수정합니다.")
    @PatchMapping("/{serverId}")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ServerUpdateResponse> updateServerInfo(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId,
                                                 @RequestBody ServerInfoUpdateRequest request) {
        Long Id = Long.parseLong(serverId);
        ServerUpdateResponse serverResponse = serverService.updateServerInfo(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "서버 이름이 변경되었습니다.", serverResponse);
    }



    @Operation(summary = "서버에 유저 초대", description = "지정한 유저를 서버에 초대합니다. 초대받은 유저에게 알림이 발송됩니다.")
    @PostMapping("/{serverId}/invite")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> inviteUserToServer(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId,
                                                   @RequestBody ServerInviteRequest request) {
        Long Id = Long.parseLong(serverId);
        serverService.inviteUser(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "참여자 초대가 완료되었습니다.", null);
    }


    @Operation(summary = "서버 초대 목록 조회", description = "해당 서버에 초대할 친구 목록들을 반환한다..")
    @GetMapping("/{serverId}/invite-friends")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<FriendInvitedListDto>> getFriendInvite(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId) {
        Long Id = Long.parseLong(serverId);
        List<FriendInvitedListDto> friends = serverService.getInvitedUsers(Id);
        log.info(friends.toString());
        return new CommonResponse<>(true, HttpStatus.OK, "초대할 친구 목록입니다.", friends);
    }


    @Operation(summary = "서버 초대 수락", description = "받은 서버 초대를 수락하고 해당 서버에 가입합니다.")
    @PostMapping("/{inviteId}/accept")
    public CommonResponse<Void> acceptInvite(@Parameter(description = "초대 ID", example = "123456789012345") @PathVariable Long inviteId) {
        serverService.acceptInvite(inviteId);
        return new CommonResponse<>(true, HttpStatus.OK, "서버 초대 수락이 완료되었습니다.", null);
    }


    @Operation(summary = "서버 알림 설정 변경", description = "해당 서버에 대한 내 알림 수신 여부를 토글합니다.")
    @PatchMapping("/{serverId}/alarm")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ServerAlarmUpdateResponse> updateAlarm(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId,
                                                                 @RequestBody @Valid ServerAlarmUpdateRequest request) {

        Long Id = Long.parseLong(serverId);
        ServerAlarmUpdateResponse response = serverService.updateAlarm(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "알림이 변경되었습니다.", response);
    }

    @Operation(summary = "서버 탈퇴", description = "로그인한 유저가 해당 서버에서 나갑니다.")
    @DeleteMapping("/{serverId}/leave")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> exitServer(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId) {
        Long Id = Long.parseLong(serverId);
        serverService.exitServer(Id);
        return new CommonResponse<>(true, HttpStatus.OK, "서버에서 나갔습니다.", null);
    }

    @Operation(summary = "서버 삭제", description = "서버를 삭제합니다. host(서버 생성자)만 수행할 수 있습니다.")
    @DeleteMapping("/{serverId}")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> deleteServer(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId) {
        Long Id = Long.parseLong(serverId);
        serverService.deleteServer(Id);
        return new CommonResponse<>(true, HttpStatus.OK, "서버를 삭제했습니다.", null);
    }


    @GetMapping("/{serverId}/members")
    public CommonResponse<List<ServerMemberResponse>> getServerMembers(@PathVariable Long serverId) {
        return new CommonResponse<>(true, HttpStatus.OK, "멤버 조회 성공", serverService.getServerMembers(serverId));
    }

}
