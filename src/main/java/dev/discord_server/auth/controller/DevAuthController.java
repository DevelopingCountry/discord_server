package dev.discord_server.auth.controller;

import dev.discord_server.auth.dto.DevLoginRequestDTO;
import dev.discord_server.auth.dto.TokenResponse;
import dev.discord_server.auth.service.AuthService;
import dev.discord_server.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 로컬 개발 전용 로그인 우회. prod 프로필에서는 이 컨트롤러 자체가 등록되지 않는다.
@Tag(name = "개발용 인증 API", description = "카카오 OAuth 없이 로그인하기 위한 개발 전용 API. prod 프로필에서는 비활성화되어 등록되지 않는다.")
@Profile("!prod")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class DevAuthController {

    private final AuthService authService;

    @Operation(summary = "개발용 로그인", description = "이메일/닉네임만으로 즉시 로그인 처리하여 Access/Refresh Token을 발급합니다. 로컬 개발 환경 전용. 인증 불필요.")
    @SecurityRequirements
    @PostMapping("/dev-login")
    public CommonResponse<TokenResponse> devLogin(@RequestBody DevLoginRequestDTO request) {
        TokenResponse tokenResponse = authService.devLogin(request.getEmail(), request.getNickname());
        return new CommonResponse<>(true, HttpStatus.OK, "개발용 로그인 완료되었습니다.", tokenResponse);
    }
}
