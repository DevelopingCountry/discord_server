package dev.discord_server.domain.channel.entity;

import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.message.entity.Message;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "channel")
public class Channel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "server_id")
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;


    @OneToMany(mappedBy = "channel")
    private Set<Message> messages = new LinkedHashSet<>();

}