package dev.discord_server.domain.server.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.server.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.discord_server.domain.server.service.ServerService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ServerController {
    private final ServerService serverService;
    @GetMapping("/servers")
    public ResponseEntity<List<ResponseDto>> getServerList() {
        List<ResponseDto> servers = serverService.findServers();
        return ResponseEntity.status(HttpStatus.OK).body(servers);
    }

    @GetMapping("/server2")
    public CommonResponse<List<ResponseDto>> getServerList2() {
        List<ResponseDto> servers = serverService.findServers();
        return new CommonResponse<>(true, HttpStatus.OK, "모든 서버가 반환되었습니다.",servers);
    }

}
