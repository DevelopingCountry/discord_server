package dev.discord_server.domain.friend.entity;

import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.friend.Enum.FriendStatus;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "friend")
@EqualsAndHashCode(of = {"fromUser", "toUser"}, callSuper = false)
public class Friend extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FriendStatus status;



}