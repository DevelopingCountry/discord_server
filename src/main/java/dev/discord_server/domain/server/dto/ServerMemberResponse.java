package dev.discord_server.domain.server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ServerMemberResponse {
    private String userId;
    private String nickname;
    private String imageUrl;
    private boolean online;
}
