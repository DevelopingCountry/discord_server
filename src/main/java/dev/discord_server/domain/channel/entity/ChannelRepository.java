package dev.discord_server.domain.channel.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findAllByServerId(Long serverId);

    boolean existsByServerIdAndName(Long serverId, String name);
}
