package dev.discord_server.domain.friend.repository;

import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByFromUserIdOrToUserId(Long fromUser, Long toUser);
    Optional<Friend> findByFromUserAndToUserOrToUserAndFromUser(User a1, User b1, User a2, User b2);
    Optional<Friend> findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
            Long fromId1, Long toId1, Long fromId2, Long toId2
    );
    @Query("""
        SELECT f
        FROM Friend f
        JOIN FETCH f.fromUser
        JOIN FETCH f.toUser
        WHERE
        (f.fromUser.id = :userId OR f.toUser.id = :userId)
        AND f.status = 'ACCEPTED'
    """)
    List<Friend> findAcceptedFriends(Long userId);

    @Query("""
SELECT COUNT(f) > 0
FROM Friend f
WHERE
(f.fromUser.id = :user1 AND f.toUser.id = :user2)
OR
(f.fromUser.id = :user2 AND f.toUser.id = :user1)
""")
    boolean existsFriend(Long user1, Long user2);
}
