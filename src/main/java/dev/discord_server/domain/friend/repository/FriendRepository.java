package dev.discord_server.domain.friend.repository;

import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, UUID> {
    List<Friend> findByFromUserOrToUser(User fromUser, User toUser);
}
