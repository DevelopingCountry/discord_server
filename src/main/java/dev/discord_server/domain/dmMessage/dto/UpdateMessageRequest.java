package dev.discord_server.domain.dmMessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMessageRequest {
    private String content;
}
