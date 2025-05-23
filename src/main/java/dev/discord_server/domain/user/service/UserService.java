package dev.discord_server.domain.user.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.nickname.repository.NicknameRepository;
import dev.discord_server.domain.user.dto.UserNickNameResponse;
import dev.discord_server.domain.user.dto.UserResponse;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final NicknameRepository nicknameRepository;

    public UserNickNameResponse changeNickname(Long uuid, String nickname) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        nicknameRepository.findByNickname(user.getNickname())
                .ifPresent(oldNicknameEntity -> {
                    oldNicknameEntity.setIsUsed(false);
                    nicknameRepository.save(oldNicknameEntity);
                });

        user.changeNickname(nickname);
        userRepository.save(user);

        return new UserNickNameResponse(user.getNickname());
    }

    public UserResponse getMyProfile(Long uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        return UserResponse.builder()
                .id(String.valueOf(user.getId()))
                .nickname(user.getNickname())
                .email(user.getEmail())
                .imageUrl(user.getImageUrl())
                .build();
    }
}
