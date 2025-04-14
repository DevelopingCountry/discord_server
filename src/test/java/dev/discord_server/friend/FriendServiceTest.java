package dev.discord_server.friend;

import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.friend.dto.FriendResponse;
import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.friend.service.FriendService;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class FriendServiceTest {

    @Autowired
    private FriendService friendService;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 친구_목록_조회_정상_동작() {
        // given: 두 유저 저장
        User userA = userRepository.save(User.builder()
                .email("a@example.com")
                .nickname("userA")
                .imageUrl("img-a")
                .role(Role.USER)
                .build());

        User userB = userRepository.save(User.builder()
                .email("b@example.com")
                .nickname("userB")
                .imageUrl("img-b")
                .role(Role.USER)
                .build());

        // 친구 관계 저장
        Friend friend = friendRepository.save(Friend.builder()
                .fromUser(userA)
                .toUser(userB)
                .status(FriendStatus.ACCEPTED)
                .build());

        // when: userA가 친구 목록 조회
        List<FriendResponse> friends = friendService.findFriends(userA.getId());

        // then: userB가 결과에 들어 있어야 함
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getName()).isEqualTo("userB");
        assertThat(friends.get(0).getImageUrl()).isEqualTo("img-b");
    }

    @Test
    void 친구_요청_성공() {
        // given
        User userA = userRepository.save(User.builder()
                .email("a@example.com")
                .nickname("userA")
                .imageUrl("img-a")
                .role(Role.USER)
                .build());

        User userB = userRepository.save(User.builder()
                .email("b@example.com")
                .nickname("userB")
                .imageUrl("img-b")
                .role(Role.USER)
                .build());

        // when
        friendService.sendFriendRequest(userA.getId(), userB.getId());

        // then
        Optional<Friend> optionalFriend = friendRepository.findByFromUserAndToUser(userA, userB);

        assertThat(optionalFriend).isPresent();
        assertThat(optionalFriend.get().getStatus()).isEqualTo(FriendStatus.PENDING);
    }

    @Test
    void 친구_요청_중복_에러() {
        // given
        User userA = userRepository.save(User.builder()
                .email("a@example.com")
                .nickname("userA")
                .imageUrl("img-a")
                .role(Role.USER)
                .build());

        User userB = userRepository.save(User.builder()
                .email("b@example.com")
                .nickname("userB")
                .imageUrl("img-b")
                .role(Role.USER)
                .build());

        friendService.sendFriendRequest(userA.getId(), userB.getId());

        // when & then
        assertThatThrownBy(() -> friendService.sendFriendRequest(userA.getId(), userB.getId()))
                .isInstanceOf(AlreadyExistElementException409.class)
                .hasMessageContaining("이미 친구 요청이 존재합니다");
    }

    @Test
    void 자기자신에게_요청시_에러() {
        User userA = userRepository.save(User.builder()
                .email("a@example.com")
                .nickname("userA")
                .imageUrl("img-a")
                .role(Role.USER)
                .build());

        assertThatThrownBy(() -> friendService.sendFriendRequest(userA.getId(), userA.getId()))
                .isInstanceOf(AlreadyExistElementException409.class)
                .hasMessageContaining("자신에게 친구 신청 할 수 없습니다.");
    }
    @Test
    void 친구_삭제_성공() {
        // given
        User userA = userRepository.save(User.builder()
                .email("a@example.com")
                .nickname("userA")
                .imageUrl("img-a")
                .role(Role.USER)
                .build());

        User userB = userRepository.save(User.builder()
                .email("b@example.com")
                .nickname("userB")
                .imageUrl("img-b")
                .role(Role.USER)
                .build());

        Friend friend = friendRepository.save(Friend.builder()
                .fromUser(userA)
                .toUser(userB)
                .status(FriendStatus.ACCEPTED)
                .build());

        // when
        friendService.deleteFriendRequest(userA.getId(), userB.getId());

        // then
        boolean exists = friendRepository.existsByFromUserAndToUser(userA, userB)
                || friendRepository.existsByFromUserAndToUser(userB, userA);

        assertThat(exists).isFalse();
    }

    @Test
    void 친구_삭제_예외_존재하지_않는_관계() {
        // given
        User userA = userRepository.save(User.builder()
                .email("a@example.com")
                .nickname("userA")
                .imageUrl("img-a")
                .role(Role.USER)
                .build());

        User userB = userRepository.save(User.builder()
                .email("b@example.com")
                .nickname("userB")
                .imageUrl("img-b")
                .role(Role.USER)
                .build());

        // when & then
        assertThatThrownBy(() -> friendService.deleteFriendRequest(userA.getId(), userB.getId()))
                .isInstanceOf(NoSuchElementFoundException404.class)
                .hasMessageContaining("존재하지 않는 친구 입니다.");
    }

    @Test
    void 친구요청_받은사람이_수락_성공() {
        // given
        User sender = userRepository.save(User.builder()
                .email("sender@test.com")
                .nickname("보낸사람")
                .role(Role.USER)
                .build());

        User receiver = userRepository.save(User.builder()
                .email("receiver@test.com")
                .nickname("받는사람")
                .role(Role.USER)
                .build());

        Friend friend = friendRepository.save(Friend.builder()
                .fromUser(sender)
                .toUser(receiver)
                .status(FriendStatus.PENDING)
                .build());

        // when
        friendService.changeFriendRequest(receiver.getId(), sender.getId(), FriendStatus.ACCEPTED);

        // then
        Friend updated = friendRepository.findById(friend.getId()).orElseThrow();
        assertThat(updated.getStatus()).isEqualTo(FriendStatus.ACCEPTED);
    }

    @Test
    void 친구요청_보낸사람이_상태변경시_403예외() {
        // given
        User sender = userRepository.save(User.builder()
                .email("sender@test.com")
                .nickname("보낸사람")
                .role(Role.USER)
                .build());

        User receiver = userRepository.save(User.builder()
                .email("receiver@test.com")
                .nickname("받는사람")
                .role(Role.USER)
                .build());

        friendRepository.save(Friend.builder()
                .fromUser(sender)
                .toUser(receiver)
                .status(FriendStatus.PENDING)
                .build());

        // when & then
        assertThatThrownBy(() -> friendService.changeFriendRequest(sender.getId(), receiver.getId(), FriendStatus.ACCEPTED))
                .isInstanceOf(ForbiddenException403.class)
                .hasMessageContaining("수락/거절할 수 있는 친구 요청이 존재하지 않습니다.");
    }

    @Test
    void 존재하지_않는_친구요청_상태변경시_예외() {
        // given
        User a = userRepository.save(User.builder()
                .email("a@test.com")
                .nickname("a")
                .role(Role.USER)
                .build());

        User b = userRepository.save(User.builder()
                .email("b@test.com")
                .nickname("b")
                .role(Role.USER)
                .build());

        // 친구 요청 자체가 없는 상태

        // when & then
        assertThatThrownBy(() -> friendService.changeFriendRequest(b.getId(), a.getId(), FriendStatus.ACCEPTED))
                .isInstanceOf(NoSuchElementFoundException404.class)
                .hasMessageContaining("친구 관계가 아닙니다.");
    }


}
