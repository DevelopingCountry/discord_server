package dev.discord_server.domain.dm.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DmVisibleRequest {
    private String dmId;
}