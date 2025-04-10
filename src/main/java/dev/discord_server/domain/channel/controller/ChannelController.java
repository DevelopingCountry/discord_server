package dev.discord_server.domain.channel.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.channel.dto.ChannelCreateRequest;
import dev.discord_server.domain.channel.dto.ChannelDeleteRequest;
import dev.discord_server.domain.channel.service.ChannelService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/server")
@AllArgsConstructor
public class ChannelController {
    private ChannelService channelService;


    @PostMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> createChannel(@PathVariable UUID serverId,
                                              @RequestBody ChannelCreateRequest request) {
        channelService.createChannel(serverId, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 생성되었습니다.",null);
    }

    @DeleteMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> deleteChannel(@PathVariable UUID serverId,
                                              @RequestBody ChannelDeleteRequest request) {

        channelService.deleteChannel(serverId, request.getChannelId());
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 삭제되었습니다.", null);
    }



}
