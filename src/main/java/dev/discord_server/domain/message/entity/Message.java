package dev.discord_server.domain.message.entity;

import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.config.BaseEntity;
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
@Table(name = "message")
public class Message extends BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content", length = 250)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void updateContent(String content) {
        this.content = content;
    }
}