package dev.discord_server.domain.user.controller;

import dev.discord_server.auth.util.JwtUtil;
import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.dto.UserNickNameResponse;
import dev.discord_server.domain.user.dto.UserNicknameRequest;
import dev.discord_server.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/me")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    @Tag(name = "유저 닉네임 변경 API", description = "[USER] 유저 닉네임 변경 API 입니다.")
    public CommonResponse<UserNickNameResponse> changeNickname(@RequestBody UserNicknameRequest request) {
        Long uuid = SecurityUtil.getCurrentUserId();
        UserNickNameResponse response = userService.changeNickname(uuid, request.getNickname());

        return new CommonResponse<>(true, HttpStatus.OK, "닉네임 변경에 성공하였습니다.", response);
    }


    @GetMapping("/token/{userId}")
    public void generateToken(@PathVariable Long userId) {
        String token1 = jwtUtil.createAccessToken(userId+1, "lwb9036", Role.USER);
        String token2 = jwtUtil.createAccessToken(userId+2, "lwb9036", Role.USER);

        log.info("token1: " + token1);
        log.info("token2: " + token2);


    }


}
