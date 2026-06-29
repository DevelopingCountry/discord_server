package dev.discord_server.domain.server.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.server.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/server")
public class ServerController {
    private final ServerService serverService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<ServerResponse>> getServerList() {
        List<ServerResponse> servers = serverService.findServers();
        return new CommonResponse<>(true, HttpStatus.OK, "모든 서버가 반환되었습니다.",servers);
    }


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ServerResponse> addServer(@RequestBody ServerCreateRequest serverCreateRequest) {
        ServerResponse serverResponse = serverService.addServer(serverCreateRequest);
        return new CommonResponse<>(true, HttpStatus.OK, "서버가 생성되었습니다.", serverResponse);
    }

    @PatchMapping("/{serverId}")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ServerUpdateResponse> updateServerInfo(@PathVariable String serverId,
                                                 @RequestBody ServerInfoUpdateRequest request) {
        Long Id = Long.parseLong(serverId);
        ServerUpdateResponse serverResponse = serverService.updateServerInfo(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "서버 이름이 변경되었습니다.", serverResponse);
    }



    @PostMapping("/{serverId}/invite")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> inviteUserToServer(@PathVariable String serverId,
                                                   @RequestBody ServerInviteRequest request) {
        Long Id = Long.parseLong(serverId);
        serverService.inviteUser(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "참여자 초대가 완료되었습니다.", null);
    }


    @PostMapping("/{inviteId}/accept")
    public CommonResponse<Void> acceptInvite(@PathVariable Long inviteId) {
        serverService.acceptInvite(inviteId);
        return new CommonResponse<>(true, HttpStatus.OK, "서버 초대 수락이 완료되었습니다.", null);
    }


    @PatchMapping("/{serverId}/alarm")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ServerAlarmUpdateResponse> updateAlarm(@PathVariable String serverId,
                                                                 @RequestBody @Valid ServerAlarmUpdateRequest request) {

        Long Id = Long.parseLong(serverId);
        ServerAlarmUpdateResponse response = serverService.updateAlarm(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "알림이 변경되었습니다.", response);
    }

    @DeleteMapping("/{serverId}/leave")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> exitServer(@PathVariable String serverId) {
        Long Id = Long.parseLong(serverId);
        serverService.exitServer(Id);
        return new CommonResponse<>(true, HttpStatus.OK, "서버에서 나갔습니다.", null);
    }

    @DeleteMapping("/{serverId}")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> deleteServer(@PathVariable String serverId) {
        Long Id = Long.parseLong(serverId);
        serverService.deleteServer(Id);
        return new CommonResponse<>(true, HttpStatus.OK, "서버를 삭제했습니다.", null);
    }


    @GetMapping("/{serverId}/members")
    public CommonResponse<List<ServerMemberResponse>> getServerMembers(@PathVariable Long serverId) {
        return new CommonResponse<>(true, HttpStatus.OK, "멤버 조회 성공", serverService.getServerMembers(serverId));
    }

}
