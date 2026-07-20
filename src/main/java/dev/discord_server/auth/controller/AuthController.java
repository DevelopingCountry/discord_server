package dev.discord_server.auth.controller;

import dev.discord_server.auth.dto.RefreshRequestDTO;
import dev.discord_server.auth.dto.TokenResponse;
import dev.discord_server.auth.service.AuthService;
import dev.discord_server.common.response.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API", description = "카카오 OAuth 로그인, Access Token 재발급 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "카카오 로그인", description = "카카오 인가 코드를 받아 로그인/회원가입을 처리하고 Access/Refresh Token을 발급합니다. 인증 불필요.")
    @SecurityRequirements
    @GetMapping("/login/kakao")
    public CommonResponse<TokenResponse> kakaoLogin(@Parameter(description = "카카오 인가 코드", example = "abcd1234") @RequestParam("code") String accessCode) {
        TokenResponse tokenResponse = authService.oAuthLogin(accessCode);
        return new CommonResponse<>(true, HttpStatus.OK, "로그인 완료되었습니다.", tokenResponse);
    }

    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 이용해 새 Access Token을 발급합니다. 인증 불필요.")
    @SecurityRequirements
    @PostMapping("/refresh")
    public CommonResponse<String> refreshAccessToken(@RequestBody RefreshRequestDTO request) {
        String newAccessToken = authService.refreshAccessToken(request.getRefreshToken());
        return new CommonResponse<>(true,HttpStatus.OK, "새 Access Token이 발급되었습니다", newAccessToken);
    }

}
