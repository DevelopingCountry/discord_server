package dev.discord_server.auth.converter;

import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.entity.User;

public class AuthConverter {
    public static User toUser(Long id, String email, String nickname, Role role) {
        return User.createUser(id, email, nickname, role);
    }
}

