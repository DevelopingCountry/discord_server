package dev.discord_server.domain.dm.entity;

import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dm")
public class Dm extends BaseEntity {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "isVisible")
    private boolean isVisible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(name = "user1_last_read_at")
    private LocalDateTime user1LastReadAt;

    @Column(name = "user2_last_read_at")
    private LocalDateTime user2LastReadAt;

    public void markRead(Long userId) {
        if (user1.getId().equals(userId)) {
            this.user1LastReadAt = LocalDateTime.now();
        } else if (user2.getId().equals(userId)) {
            this.user2LastReadAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getLastReadAt(Long userId) {
        if (user1.getId().equals(userId)) return user1LastReadAt;
        if (user2.getId().equals(userId)) return user2LastReadAt;
        return null;
    }

}