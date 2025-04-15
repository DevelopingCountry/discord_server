package dev.discord_server.domain.serverUser.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServerUserRepository extends JpaRepository<ServerUser, Long> {
    Optional<ServerUser> findByServerIdAndUserId(Long serverId, Long currentUserId);
}
