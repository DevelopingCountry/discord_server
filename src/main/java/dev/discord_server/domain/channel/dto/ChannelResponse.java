package dev.discord_server.domain.channel.dto;

import dev.discord_server.domain.channel.entity.Channel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChannelResponse {
    @Schema(description = "채널 ID", example = "123456789012345")
    private String id;
    @Schema(description = "채널 이름", example = "일반")
    private String name;
    @Schema(description = "채널 타입", example = "CHAT")
    private String type;
    @Schema(description = "채널 생성자 유저 ID", example = "123456789012345")
    private String creatorId;

    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
                String.valueOf(channel.getId()),
                channel.getName(),
                channel.getType().name(), // enum -> string
                String.valueOf(channel.getCreator().getId())
        );
    }
}
