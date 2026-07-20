package dev.discord_server.domain.dm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DmVisibleResponse {
    @Schema(description = "DM 대화 ID", example = "123456789012345")
    Long id;
    @Schema(description = "변경된 표시 여부", example = "false")
    boolean isVisible;
}
