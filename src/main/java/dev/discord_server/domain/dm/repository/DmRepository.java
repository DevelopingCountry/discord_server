package dev.discord_server.domain.dm.repository;

import dev.discord_server.domain.dm.entity.Dm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DmRepository extends JpaRepository<Dm, Long> {
    List<Dm> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);

    Optional<Dm> findByUser1IdAndUser2IdOrUser2IdAndUser1Id(Long userId, Long targetUserId, Long userId1, Long targetUserId1);
}
