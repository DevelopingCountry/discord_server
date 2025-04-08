package dev.discord_server.domain.server.entity;

import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.serverUser.entity.ServerUser;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "server")
public class Server extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "image_url", nullable = false, length = 50)
    private String imageUrl;


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

    @OneToMany(mappedBy = "server")
    private Set<Channel> channels = new LinkedHashSet<>();

    @OneToMany(mappedBy = "server")
    private Set<ServerUser> serverUsers = new LinkedHashSet<>();



}