package dev.discord_server.domain.message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {
    private String channelId;
    private String messageId;
    private String userId;
    private String nickName;
    private String imageUrl;
    private String content;
    private LocalDateTime createdAt;
}
