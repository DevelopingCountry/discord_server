package dev.discord_server.domain.user.controller;

import dev.discord_server.auth.util.JwtUtil;
import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.dto.*;
import dev.discord_server.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "유저 API", description = "내 프로필 조회, 닉네임 변경 API")
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/me")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "내 프로필 조회", description = "로그인한 유저 본인의 프로필 정보를 반환합니다.")
    @GetMapping
    public CommonResponse<UserResponse> getMyProfile() {
        Long uuid = SecurityUtil.getCurrentUserId();
        UserResponse response = userService.getMyProfile(uuid);

        return new CommonResponse<>(true, HttpStatus.OK, "닉네임 변경에 성공하였습니다.", response);
    }
    @Operation(summary = "프로필 변경", description = "로그인한 유저 본인의 닉네임/프로필 이미지를 변경합니다.")
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<UserProfileResponse> changeProfile(@RequestBody UserProfileRequest request) {
        Long uuid = SecurityUtil.getCurrentUserId();
        UserProfileResponse response = userService.changeProfile(uuid, request);

        return new CommonResponse<>(true, HttpStatus.OK, "프로필이 변경되었습니다.", response);
    }



    @Hidden
    @GetMapping("/token/{userId}")
    public void generateToken(@PathVariable Long userId) {
        String token1 = jwtUtil.createAccessToken(userId+1, "lwb9036", Role.USER);
        String token2 = jwtUtil.createAccessToken(userId+2, "lwb9036", Role.USER);

        log.info("token1: " + token1);
        log.info("token2: " + token2);


    }



}
