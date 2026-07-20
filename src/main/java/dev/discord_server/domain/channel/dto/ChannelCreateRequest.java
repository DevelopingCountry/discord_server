package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.Enum.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;


@Getter
public class ChannelCreateRequest {
    @Schema(description = "채널 이름", example = "일반")
    private String channelName;
    @Schema(description = "채널 타입", example = "CHAT")
    private ChannelType type; // CHAT or VOICE
}
