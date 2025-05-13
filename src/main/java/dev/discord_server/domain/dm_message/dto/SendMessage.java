package dev.discord_server.domain.dm_message.dto;

import lombok.*;


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
