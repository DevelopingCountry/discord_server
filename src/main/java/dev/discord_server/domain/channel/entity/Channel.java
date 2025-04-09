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

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "channel")
public class Channel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "server_id")
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creator_id",
            nullable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User creator;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Lob
    @Column(name = "type", nullable = false)
    private String type;


    @OneToMany(mappedBy = "channel")
    private List<Message> messages = new ArrayList<>();

}