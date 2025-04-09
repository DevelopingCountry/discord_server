package dev.discord_server.domain.friend.repository;

import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, UUID> {
    List<Friend> findByFromUserIdOrToUserId(UUID fromUser, UUID toUser);
    Optional<Friend> findByFromUserAndToUser(User fromUser, User toUser);
    boolean existsByFromUserAndToUser(User fromUser, User toUser);
    Optional<Friend> findByFromUserAndToUserOrToUserAndFromUser(User a1, User b1, User a2, User b2);

}
