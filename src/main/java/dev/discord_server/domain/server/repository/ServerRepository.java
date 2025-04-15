package dev.discord_server.domain.server.repository;

import dev.discord_server.domain.server.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {

    List<Server> findByServerUsers_User_Id(Long userId);
}
