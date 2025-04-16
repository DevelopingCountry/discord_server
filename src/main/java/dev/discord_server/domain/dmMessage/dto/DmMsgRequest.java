package dev.discord_server.domain.dmMessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DmMsgRequest {
    private String content;
}