package dev.discord_server.domain.dm_message.repository;

import dev.discord_server.domain.dm_message.entity.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {
    List<DmMessage> findByDmIdOrderByCreatedAtAsc(Long dmId);
}
