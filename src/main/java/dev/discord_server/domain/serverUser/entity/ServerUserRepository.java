package dev.discord_server.domain.serverUser.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServerUserRepository extends JpaRepository<ServerUser, UUID> {
}
