package dev.discord_server.domain.dm_message.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DmMessageResponse {
    private String dmId;
    private String messageId;
    private String userId;
    private String nickName;
    private String imageUrl;
    private String content;
    private LocalDateTime createdAt;
}
