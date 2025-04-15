package dev.discord_server.domain.dmMessage.repository;

import dev.discord_server.domain.dmMessage.entity.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {
    List<DmMessage> findByDmIdOrderByCreatedAtAsc(Long dmId);
}
