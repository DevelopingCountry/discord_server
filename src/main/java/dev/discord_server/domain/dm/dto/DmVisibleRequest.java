package dev.discord_server.domain.dm.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DmVisibleRequest {
    @Schema(description = "숨김/표시를 토글할 DM 대화 ID", example = "123456789012345")
    private String dmId;
}