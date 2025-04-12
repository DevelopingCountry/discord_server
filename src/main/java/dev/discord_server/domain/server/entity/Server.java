package dev.discord_server.domain.server.entity;

import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.serverUser.entity.ServerUser;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "server")
public class Server extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 20)
    private String serverName;

    @Column(name = "image_url", nullable = false, length = 50)
    private String image;


    /*
    foreignKey의 제약조건을 제외함으로써 조회는 가볍게 할 수 있음
     */
    @ManyToOne
    @JoinColumn(
            name = "host_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User host;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Channel> channels = new LinkedHashSet<>();

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ServerUser> serverUsers = new LinkedHashSet<>();


    //TODO: host_id로 안해도되는지
    public static Server createServer(String name, String image, User host) {
        return Server.builder()
                .serverName(name)
                .image(image)
                .host(host)
                .build();
    }


}