package dev.discord_server.domain.message.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChannelMsgRequest {
    private String content;
}
