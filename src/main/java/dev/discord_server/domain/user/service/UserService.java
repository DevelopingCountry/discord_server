package dev.discord_server.domain.user.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.user.dto.UserNickNameResponse;
import dev.discord_server.domain.user.dto.UserResponse;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;


    public UserNickNameResponse changeNickname(Long uuid, String nickname) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        user.changeNickname(nickname);
        userRepository.save(user);
        return new UserNickNameResponse(user.getNickname());
    }
}
