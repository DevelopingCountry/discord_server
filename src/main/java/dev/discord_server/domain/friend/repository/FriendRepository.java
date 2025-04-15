package dev.discord_server.domain.friend.repository;

import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByFromUserIdOrToUserId(Long fromUser, Long toUser);
    Optional<Friend> findByFromUserAndToUser(User fromUser, User toUser);
    boolean existsByFromUserAndToUser(User fromUser, User toUser);
    Optional<Friend> findByFromUserAndToUserOrToUserAndFromUser(User a1, User b1, User a2, User b2);
    Optional<Friend> findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
            Long fromId1, Long toId1, Long fromId2, Long toId2
    );

}
