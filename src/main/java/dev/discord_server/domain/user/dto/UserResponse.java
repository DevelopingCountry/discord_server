package dev.discord_server.domain.user.dto;

import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String imageUrl;
    private Role role;

    @Builder
    public UserResponse(User user) {
        this.id = user.getId();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.imageUrl = user.getImageUrl();
        this.role = user.getRole(); // Enum이면 .name() 또는 toString()
    }
}
