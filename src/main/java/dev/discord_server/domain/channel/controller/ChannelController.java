package dev.discord_server.domain.channel.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.channel.dto.ChannelCreateRequest;
import dev.discord_server.domain.channel.dto.ChannelDeleteRequest;
import dev.discord_server.domain.channel.dto.ChannelResponse;
import dev.discord_server.domain.channel.dto.ChannelUpdateRequest;
import dev.discord_server.domain.channel.service.ChannelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채널 API", description = "서버 내 채널(CHAT/VOICE) 생성/조회/수정/삭제 API")
@RestController
@RequestMapping("/server")
@AllArgsConstructor
public class ChannelController {
    private ChannelService channelService;

    @Operation(summary = "채널 목록 조회", description = "서버 내 채널 목록을 반환합니다.")
    @GetMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<List<ChannelResponse>> getListChannel(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId) {
        Long Id = Long.parseLong(serverId);
        List<ChannelResponse> channels = channelService.findChannels(Id);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 채널이 반환되었습니다.",channels);
    }


    @Operation(summary = "채널 생성", description = "서버 내에 텍스트(CHAT) 또는 음성(VOICE) 채널을 생성합니다.")
    @PostMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ChannelResponse> createChannel(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId,
                                                               @RequestBody ChannelCreateRequest request) {
        Long Id = Long.parseLong(serverId);
        ChannelResponse channel = channelService.createChannel(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 생성되었습니다.",channel);
    }

    @Operation(summary = "채널 삭제", description = "서버 내 채널을 삭제합니다.")
    @DeleteMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> deleteChannel(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId,
                                              @RequestBody ChannelDeleteRequest request) {

        Long Id = Long.parseLong(serverId);
        channelService.deleteChannel(Id, request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 삭제되었습니다.", null);
    }

    @Operation(summary = "채널 이름 수정", description = "서버 내 채널의 이름을 수정합니다.")
    @PatchMapping("/{serverId}/channel")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<ChannelResponse> updateChannel(@Parameter(description = "서버 ID", example = "123456789012345") @PathVariable String serverId,
                                              @RequestBody ChannelUpdateRequest request){
        Long Id = Long.parseLong(serverId);
        ChannelResponse channel = channelService.updateChannel(Id,request);
        return new CommonResponse<>(true, HttpStatus.OK, "채널이 수정되었습니다.", channel);
    }



}
