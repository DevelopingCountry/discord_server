package dev.discord_server.domain.channel.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.channel.dto.ChannelCreateRequest;
import dev.discord_server.domain.channel.dto.ChannelDeleteRequest;
import dev.discord_server.domain.channel.dto.ChannelResponse;
import dev.discord_server.domain.channel.dto.ChannelUpdateRequest;
import dev.discord_server.domain.channel.service.ChannelService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/server")
@AllArgsConstructor
public class ChannelController {
    private ChannelService channelService;

    @GetMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<ChannelResponse>> getListChannel(@PathVariable String serverId) {
        Long Id = Long.parseLong(serverId);
        List<ChannelResponse> channels = channelService.findChannels(Id);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 채널이 반환되었습니다.",channels);
    }


    @PostMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ChannelResponse> createChannel(@PathVariable String serverId,
                                                               @RequestBody ChannelCreateRequest request) {
        Long Id = Long.parseLong(serverId);
        ChannelResponse channel = channelService.createChannel(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 생성되었습니다.",channel);
    }

    @DeleteMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> deleteChannel(@PathVariable String serverId,
                                              @RequestBody ChannelDeleteRequest request) {

        Long Id = Long.parseLong(serverId);
        channelService.deleteChannel(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 삭제되었습니다.", null);
    }

    @PatchMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ChannelResponse> updateChannel(@PathVariable String serverId,
                                              @RequestBody ChannelUpdateRequest request){
        Long Id = Long.parseLong(serverId);
        ChannelResponse channel = channelService.updateChannel(Id,request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 수정되었습니다.", channel);
    }



}
