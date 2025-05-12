package dev.discord_server.domain.dm_message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateDMMessageRequest {
    private String content;
}
