package dev.discord_server.domain.dmMessage.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SendMessage {
    private String dmId;
    private String messageId;
    private String nickName;
    private String imageUrl;
    private String content;
    private String createdAt;
}
