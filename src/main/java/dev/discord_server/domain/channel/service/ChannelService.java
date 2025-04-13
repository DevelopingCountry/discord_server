package dev.discord_server.domain.channel.service;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.exception.custom.exception.PreconditionFailException412;
import dev.discord_server.domain.channel.dto.ChannelCreateRequest;
import dev.discord_server.domain.channel.dto.ChannelCreateResponse;
import dev.discord_server.domain.channel.dto.ChannelDeleteRequest;
import dev.discord_server.domain.channel.dto.ChannelResponse;
import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.domain.channel.entity.ChannelRepository;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.server.repository.ServerRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChannelService {
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;



    @Transactional
    public ChannelCreateResponse createChannel(UUID serverId, ChannelCreateRequest request) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        UUID currentUserId = SecurityUtil.getCurrentUserId();

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Channel channel = Channel.builder()
                .server(server)
                .creator(creator)
                .name(request.getChannelName())
                .type(request.getType())
                .build();

        channelRepository.save(channel);

        return new ChannelCreateResponse(
                channel.getId(),
                channel.getName(),
                channel.getType()
        );
    }


    @Transactional
    public void deleteChannel(UUID serverId, ChannelDeleteRequest request) {
        UUID hostId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));


        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_CHANNEL));

        if(!channel.getServer().getId().equals(server.getId())) {
            throw new PreconditionFailException412(ErrorDefineCode.CHANNEL_NOT_IN_SERVER);
        }

        if(!channel.getCreator().getId().equals(hostId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        channelRepository.delete(channel);
    }

    public List<ChannelResponse> findChannels(UUID serverId) {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        return server.getChannels().stream()
                .map(ChannelResponse::from)
                .toList();

    }
}
