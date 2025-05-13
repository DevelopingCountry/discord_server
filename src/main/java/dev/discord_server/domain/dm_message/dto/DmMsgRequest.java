package dev.discord_server.domain.dm_message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DmMsgRequest {
    private String content;
}