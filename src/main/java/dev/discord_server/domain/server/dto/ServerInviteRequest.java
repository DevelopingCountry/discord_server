package dev.discord_server.domain.server.dto;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ServerInviteRequest {
    private String guestId;
}
