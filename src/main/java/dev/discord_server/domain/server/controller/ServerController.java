package dev.discord_server.domain.server.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.server.dto.ServerResponse;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public CommonResponse<List<ServerResponse>> getServerList2() {
        UUID userId = SecurityUtil.getCurrentUserId();
        List<ServerResponse> servers = serverService.findServers(userId);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 서버가 반환되었습니다.",servers);
    }
}
