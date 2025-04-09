package dev.discord_server.friend;

import dev.discord_server.config.exception.custom.CustomExceptionHandler;
import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.domain.dto.FriendResponse;
import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.friend.service.FriendService;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
        List<Friend> results = friendRepository.findByFromUserAndToUser(userA, userB);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getStatus()).isEqualTo(FriendStatus.PENDING);
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

}
