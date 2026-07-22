package dev.discord_server.domain.dm_message.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateDMMessageRequest {
    @Schema(description = "변경할 메시지 내용", example = "수정된 메시지입니다")
    private String content;
}
