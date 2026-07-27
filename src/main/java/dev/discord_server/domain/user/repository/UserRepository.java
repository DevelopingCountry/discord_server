package dev.discord_server.domain.user.repository;

import dev.discord_server.domain.server.dto.FriendInvitedListDto;
import dev.discord_server.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);

    @Query("""
    SELECT new dev.discord_server.domain.server.dto.FriendInvitedListDto(
        str(u.id),
        u.nickname,
        u.imageUrl,
        true
    )
    FROM User u
    WHERE u.id IN :ids
""")
    List<FriendInvitedListDto> findInviteY(@Param("ids") List<Long> ids);



    @Query("""
    SELECT new dev.discord_server.domain.server.dto.FriendInvitedListDto(
        str(u.id),
        u.nickname,
        u.imageUrl,
        false
    )
    FROM User u
    WHERE u.id IN :ids
""")
    List<FriendInvitedListDto> findInviteN(@Param("ids") List<Long> ids);

    boolean existsByNickname(String nickname);
}
