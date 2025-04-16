package dev.discord_server.domain.nickname.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "nicknames")
@Getter
@Setter
@NoArgsConstructor
public class Nickname {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nickname;

    @Column(name = "is_used")
    private Boolean isUsed = false;
}
