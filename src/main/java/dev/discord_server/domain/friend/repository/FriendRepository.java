package dev.discord_server.domain.friend.repository;

import dev.discord_server.domain.friend.entity.Friend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, UUID> {
}
