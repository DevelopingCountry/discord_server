package dev.discord_server.domain.user.entity;

import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.message.entity.Message;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.serverUser.entity.ServerUser;
import dev.discord_server.domain.user.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Getter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "password", length = 20)
    private String password;

    @Column(name = "email", nullable = false, length = 20)
    private String email;

    @Column(name="image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "host")
    @Builder.Default
    private List<Server> hostedServers = new ArrayList<>();

    @OneToMany(mappedBy = "creator")
    @Builder.Default
    private List<Channel> createdChannels = new ArrayList<>();


    @OneToMany(mappedBy = "fromUser")
    @Builder.Default
    private Set<Friend> to_friends = new LinkedHashSet<>();

    @OneToMany(mappedBy = "toUser")
    @Builder.Default
    private Set<Friend> from_friends = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private Set<ServerUser> serverUsers = new LinkedHashSet<>();



    public static User createUser(String email, String nickname, Role role) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .role(role)
                .build();
    }

    public void changeNickname(String nickname) {
        this.nickname = nickname;
    }

}