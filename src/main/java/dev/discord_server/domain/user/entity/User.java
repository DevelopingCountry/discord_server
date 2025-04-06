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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nickname", nullable = false, length = 10)
    private String nickname;

    @Column(name = "password", length = 20)
    private String password;

    @Column(name = "email", nullable = false, length = 20)
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "host")
    private List<Server> hostedServers;

    @OneToMany(mappedBy = "creator")
    private List<Channel> createdChannels;


    @OneToMany(mappedBy = "fromUser")
    private Set<Friend> to_friends = new LinkedHashSet<>();

    @OneToMany(mappedBy = "toUser")
    private Set<Friend> from_friends = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Message> messages = new LinkedHashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<ServerUser> serverUsers = new LinkedHashSet<>();



    public static User createUser(String email, String nickname, Role role) {
        return User.builder()
                .email(email)
                .nickname(nickname)
                .role(role)
                .build();
    }

}