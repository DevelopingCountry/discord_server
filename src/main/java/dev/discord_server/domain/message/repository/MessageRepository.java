package dev.discord_server.domain.message.repository;

import dev.discord_server.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message,Long> {
    List<Message> findByChannelIdOrderByCreatedAtAsc(Long channelId);
}
