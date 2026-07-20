package dev.discord_server.domain.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import java.util.UUID;

@Getter
public class ChannelDeleteRequest {
    @Schema(description = "삭제할 채널 ID", example = "123456789012345")
    private String channelId;
}
