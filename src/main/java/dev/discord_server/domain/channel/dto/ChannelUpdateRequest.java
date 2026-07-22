package dev.discord_server.domain.channel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ChannelUpdateRequest {
    @Schema(description = "수정할 채널 ID", example = "123456789012345")
    private Long channelId;
    @Schema(description = "변경할 채널 이름", example = "공지사항")
    private String channelName;
}