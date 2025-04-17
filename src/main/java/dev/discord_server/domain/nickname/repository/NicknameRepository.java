package dev.discord_server.domain.nickname.repository;

import dev.discord_server.domain.nickname.entity.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NicknameRepository extends JpaRepository<Nickname,Long> {
    @Query(value = "SELECT * FROM nicknames WHERE is_used = FALSE ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Nickname> getRandomUnusedNickname();

    Optional<Nickname> findByNickname(String nickname);
}
