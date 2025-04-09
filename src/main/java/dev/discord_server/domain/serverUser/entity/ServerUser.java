    package dev.discord_server.domain.serverUser.entity;

    import dev.discord_server.config.BaseEntity;
    import dev.discord_server.domain.server.entity.Server;
    import dev.discord_server.domain.user.entity.User;
    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.OnDelete;
    import org.hibernate.annotations.OnDeleteAction;
    import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "server_user")
@EqualsAndHashCode(of = {"user", "server"}, callSuper = false)
public class ServerUser extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        @Column(name = "id", columnDefinition = "BINARY(16)")
        private UUID id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @OnDelete(action = OnDeleteAction.CASCADE)
        @JoinColumn(name = "server_id", nullable = false)
        private Server server;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "user_id", nullable = false)
        private User user;

        @Column(name = "alarm", nullable = false)
        private boolean alarm;


    }