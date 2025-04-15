package dev.discord_server.domain.channel.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.channel.dto.ChannelCreateRequest;
import dev.discord_server.domain.channel.dto.ChannelCreateResponse;
import dev.discord_server.domain.channel.dto.ChannelDeleteRequest;
import dev.discord_server.domain.channel.dto.ChannelResponse;
import dev.discord_server.domain.channel.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/server")
@AllArgsConstructor
public class ChannelController {
    private ChannelService channelService;

    @GetMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<ChannelResponse>> getListChannel(@PathVariable Long serverId) {
        List<ChannelResponse> channels = channelService.findChannels(serverId);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 채널이 반환되었습니다.",channels);
    }


    @PostMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ChannelCreateResponse> createChannel(@PathVariable Long serverId,
                                                               @RequestBody ChannelCreateRequest request) {
        ChannelCreateResponse channel = channelService.createChannel(serverId, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 생성되었습니다.",channel);
    }

    @DeleteMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> deleteChannel(@PathVariable Long serverId,
                                              @RequestBody ChannelDeleteRequest request) {

        channelService.deleteChannel(serverId, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 삭제되었습니다.", null);
    }



}
