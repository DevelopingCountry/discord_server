package dev.discord_server.domain.dmMessage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DmMessageResponse {
    private Long dmId;
    private String nickName;
    private String imageUrl;
    private String content;
    private LocalDateTime createdAt;
}
