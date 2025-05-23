package dev.discord_server.domain.user.dto;

import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class UserResponse {
    private String id;
    private String email;
    private String nickname;
    private String imageUrl;


}
