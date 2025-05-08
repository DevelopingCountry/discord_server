package dev.discord_server.domain.server.repository;

import dev.discord_server.domain.server.entity.ServerInvite;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServerInviteRepository extends JpaRepository<ServerInvite, Long> {
}
