package dev.discord_server.domain.dmMessage.entity;

import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.dm.entity.Dm;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Table(name = "dm_message")
public class DmMessage extends BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "content", length = 250)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dm_id", nullable = false)
    private Dm dm;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public void updateContent(String content) {
        this.content = content;
    }
}

