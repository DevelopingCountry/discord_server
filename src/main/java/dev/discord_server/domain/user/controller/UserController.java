package dev.discord_server.domain.user.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.user.dto.UserNickNameResponse;
import dev.discord_server.domain.user.dto.UserNicknameRequest;
import dev.discord_server.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/me")
public class UserController {
    private final UserService userService;

    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<UserNickNameResponse> changeNickname(@RequestBody UserNicknameRequest request) {
        UUID uuid = SecurityUtil.getCurrentUserId();
        UserNickNameResponse response = userService.changeNickname(uuid, request.getNickname());

        return new CommonResponse<>(true, HttpStatus.OK, "닉네임 변경에 성공하였습니다.", response);
    }
}
