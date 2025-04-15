package dev.discord_server.domain.dmMessage.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessage {
    private Long dmId;
    private Long messageId;
    private String nickName;
    private String imageUrl;
    private String content;
    private String createdAt;
}
