package dev.discord_server.domain.server.dto;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ServerInviteRequest {
    private UUID guestId;
}
