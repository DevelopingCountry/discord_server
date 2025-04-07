package dev.discord_server.domain.server.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.server.dto.ServerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
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
public class ServerController {
    private final ServerService serverService;

    @GetMapping("/server")
    @PreAuthorize("hasRole('ADMIN')")
    public CommonResponse<List<ServerResponse>> getServerList2() {
        List<ServerResponse> servers = serverService.findServers();
        return new CommonResponse<>(true, HttpStatus.OK, "모든 서버가 반환되었습니다.",servers);
    }

}
