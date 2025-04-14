package dev.discord_server.domain.server.repository;

import dev.discord_server.domain.server.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServerRepository extends JpaRepository<Server, UUID> {

    List<Server> findByServerUsers_User_Id(UUID userId);
}
