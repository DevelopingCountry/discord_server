package dev.discord_server.domain.channel.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.channel.dto.ChannelCreateRequest;
import dev.discord_server.domain.channel.dto.ChannelDeleteRequest;
import dev.discord_server.domain.channel.service.ChannelService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ChannelController {
    private ChannelService channelService;


    @PostMapping("/server/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> createChannel(@PathVariable UUID serverId,
                                              @RequestBody ChannelCreateRequest request) {
        channelService.createChannel(serverId, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 생성되었습니다.",null);
    }

    @DeleteMapping("/server/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> deleteChannel(@PathVariable UUID serverId,
                                              @RequestBody ChannelDeleteRequest request) {
        UUID hostId = SecurityUtil.getCurrentUserId(); // 인증 정보에서 가져옴
        channelService.deleteChannel(serverId, hostId, request.getChannelId());
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 삭제되었습니다.", null);
    }



}
