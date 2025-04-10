package dev.discord_server.domain.server.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.server.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import dev.discord_server.domain.server.service.ServerService;

import java.util.List;
import java.util.UUID;


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
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        List<ServerResponse> servers = serverService.findServers(currentUserId);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 서버가 반환되었습니다.",servers);
    }


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<UUID> addServer(@RequestBody ServerRequest serverRequest) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        UUID serverId = serverService.addServer(currentUserId, serverRequest);
        return new CommonResponse<>(true, HttpStatus.OK, "서버가 생성되었습니다.", serverId);
    }

    @PatchMapping("/{serverId}")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<String> updateServerName(@PathVariable UUID serverId,
                                                 @RequestBody ServerNameUpdateRequest request) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        serverService.updateServerName(currentUserId, serverId, request.getName());
        return new CommonResponse<>(true, HttpStatus.OK, "서버 이름이 변경되었습니다.", null);
    }


    @PatchMapping("/{serverId}/image")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> updateServerImage(@PathVariable UUID serverId,
                                                  @RequestBody ServerImageUpdateRequest request) {
        serverService.updateServerImage(serverId, request);
        return new CommonResponse<>(true, HttpStatus.OK, "서버 이미지가 변경되었습니다.",null);
    }


    @PostMapping("/{serverId}/invite")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> inviteUserToServer(@PathVariable UUID serverId,
                                                   @RequestBody ServerInviteRequest request) {
        serverService.inviteUser(serverId, request);
        return new CommonResponse<>(true, HttpStatus.OK, "참여자 초대가 완료되었습니다.", null);
    }





}
