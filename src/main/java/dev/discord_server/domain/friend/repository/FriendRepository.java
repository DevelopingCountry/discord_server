package dev.discord_server.domain.friend.repository;

import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findByFromUserAndToUserOrToUserAndFromUser(User a1, User b1, User a2, User b2);
    Optional<Friend> findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
            Long fromId1, Long toId1, Long fromId2, Long toId2
    );
    @Query("SELECT DISTINCT f FROM Friend f " +
            "WHERE (f.fromUser.id = :userId OR f.toUser.id = :userId) " +
            "ORDER BY f.createdAt DESC")
    List<Friend> findDistinctFriendsByUserId(Long userId);

}
