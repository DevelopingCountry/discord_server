package dev.discord_server.domain.user.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.nickname.repository.NicknameRepository;
import dev.discord_server.domain.user.dto.UserProfileRequest;
import dev.discord_server.domain.user.dto.UserProfileResponse;
import dev.discord_server.domain.user.dto.UserResponse;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final NicknameRepository nicknameRepository;

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

    public UserProfileResponse changeProfile(Long uuid, UserProfileRequest request) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        boolean nicknameChanged = !user.getNickname().equals(request.getNickname());
        if (nicknameChanged && userRepository.existsByNickname(request.getNickname())) {
            throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_USERNAME);
        }

        if (nicknameChanged) {
            nicknameRepository.findByNickname(user.getNickname())
                    .ifPresent(oldNicknameEntity -> {
                        oldNicknameEntity.setIsUsed(false);
                        nicknameRepository.save(oldNicknameEntity);
                    });
        }
        user.changeProfile(request.getNickname(), request.getImageUrl());
        userRepository.save(user);

        return new UserProfileResponse(user.getNickname(), user.getImageUrl());
    }
}
