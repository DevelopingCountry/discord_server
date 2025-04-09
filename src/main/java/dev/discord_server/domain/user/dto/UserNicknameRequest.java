package dev.discord_server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserNicknameRequest {
    private String nickname;
}
