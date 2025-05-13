package dev.discord_server.auth.controller;

import dev.discord_server.auth.dto.RefreshRequestDTO;
import dev.discord_server.auth.dto.TokenResponse;
import dev.discord_server.auth.service.AuthService;
import dev.discord_server.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login/kakao")
    public CommonResponse<TokenResponse> kakaoLogin(@RequestParam("code") String accessCode) {
        TokenResponse tokenResponse = authService.oAuthLogin(accessCode);
        return new CommonResponse<>(true, HttpStatus.OK, "로그인 완료되었습니다.", tokenResponse);
    }

    // Refresh Token을 이용한 Access Token 재발급
    @PostMapping("/refresh")
    public CommonResponse<String> refreshAccessToken(@RequestBody RefreshRequestDTO request) {
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        return new CommonResponse<>(true,HttpStatus.OK, "새 Access Token이 발급되었습니다", newAccessToken);
    }

}
