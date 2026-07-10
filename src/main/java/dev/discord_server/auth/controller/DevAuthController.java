package dev.discord_server.auth.controller;

import dev.discord_server.auth.dto.DevLoginRequestDTO;
import dev.discord_server.auth.dto.TokenResponse;
import dev.discord_server.auth.service.AuthService;
import dev.discord_server.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 로컬 개발 전용 로그인 우회. prod 프로필에서는 이 컨트롤러 자체가 등록되지 않는다.
@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class DevAuthController {

    private final AuthService authService;

    @PostMapping("/dev-login")
    public CommonResponse<TokenResponse> devLogin(@RequestBody DevLoginRequestDTO request) {
        TokenResponse tokenResponse = authService.devLogin(request.getEmail(), request.getNickname());
        return new CommonResponse<>(true, HttpStatus.OK, "개발용 로그인 완료되었습니다.", tokenResponse);
    }
}
