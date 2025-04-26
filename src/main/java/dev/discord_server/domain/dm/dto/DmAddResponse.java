package dev.discord_server.domain.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DmAddResponse {
    private String dmId;
    private String targetId;
    private String targetImageUrl;
    private String targetNickname;
}
