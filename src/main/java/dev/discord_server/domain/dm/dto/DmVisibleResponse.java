package dev.discord_server.domain.dm.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DmVisibleResponse {
    Long id;
    boolean isVisible;
}
