package dev.discord_server.domain.server.entity;

import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.server.entity.Enum.InviteStatus;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServerInvite extends BaseEntity {
    @Id
    private Long id;

    @ManyToOne
    private User fromUser;

    @ManyToOne
    private User toUser;

    @ManyToOne
    private Server server;

    @Enumerated(EnumType.STRING)
    private InviteStatus status; // PENDING, ACCEPTED, DECLINED

}
