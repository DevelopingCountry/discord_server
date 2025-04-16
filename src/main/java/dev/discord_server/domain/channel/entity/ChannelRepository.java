package dev.discord_server.domain.channel.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findAllByServerId(Long serverId);
}
