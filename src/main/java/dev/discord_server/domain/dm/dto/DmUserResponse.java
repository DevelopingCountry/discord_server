package dev.discord_server.domain.dm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class DmUserResponse {
    private Long dmId;
    private Long targetId;
    private String targetImageUrl;
    private String targetNickname;
}
