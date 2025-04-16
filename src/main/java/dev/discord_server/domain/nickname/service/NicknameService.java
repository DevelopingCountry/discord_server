package dev.discord_server.domain.nickname.service;

import dev.discord_server.domain.nickname.entity.Nickname;
import dev.discord_server.domain.nickname.repository.NicknameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NicknameService {
    private final NicknameRepository nicknameRepository;

    @Transactional
    public String assignRandomNickname() {
        Nickname nickname = nicknameRepository.getRandomUnusedNickname()
                .orElseThrow(() -> new IllegalStateException("No available nicknames left"));

        nickname.setIsUsed(true);
        nicknameRepository.save(nickname);

        return nickname.getNickname();
    }
}
