package dev.discord_server.domain.message.entity;

import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.config.BaseEntity;
import dev.discord_server.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "message")
public class Message extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "content", length = 50)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    // 답글 기능: 현재 메시지의 부모 메시지를 참조하는 컬럼
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_message_id")
    private Message parentMessage;

    // 답글 기능: 현재 메시지를 부모로 가지는 답글 리스트
    @OneToMany(mappedBy = "parentMessage")
    private List<Message> replies;

    // 쓰레드 기능: 쓰레드의 루트 메시지를 참조하는 컬럼
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_root_id")
    private Message threadRoot;

    // 쓰레드 기능: 동일한 쓰레드 루트를 가지는 메시지 리스트
    @OneToMany(mappedBy = "threadRoot")
    private List<Message> threadReplies;

}