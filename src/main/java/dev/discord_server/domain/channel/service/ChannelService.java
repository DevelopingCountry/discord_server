package dev.discord_server.domain.channel.service;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.exception.custom.exception.PreconditionFailException412;
import dev.discord_server.config.redis.ChannelRedisPublisher;
import dev.discord_server.domain.channel.dto.*;
import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.domain.channel.entity.ChannelRepository;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.server.repository.ServerRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChannelService {
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final ChannelRedisPublisher channelRedisPublisher;



    @Transactional
    public ChannelResponse createChannel(Long serverId, ChannelCreateRequest request) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        Long currentUserId = SecurityUtil.getCurrentUserId();

        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));


        Channel channel = Channel.builder()
                .id(snowflakeIdGenerator.generateId())
                .server(server)
                .creator(creator)
                .name(request.getChannelName())
                .type(request.getType())
                .build();

        channelRepository.save(channel);

        // WebSocket 알림 전파
        channelRedisPublisher.publish(ChannelCreatedOrUpdateMsgResponse.from(channel, serverId));

        return ChannelResponse.from(channel);
    }


    @Transactional
    public void deleteChannel(Long serverId, ChannelDeleteRequest request) {
        Long hostId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));


        Channel channel = channelRepository.findById(Long.valueOf(request.getChannelId()))
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_CHANNEL));

        if(!channel.getServer().getId().equals(server.getId())) {
            throw new PreconditionFailException412(ErrorDefineCode.CHANNEL_NOT_IN_SERVER);
        }

        if(!channel.getCreator().getId().equals(hostId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        channelRepository.delete(channel);
    }

    public List<ChannelResponse> findChannels(Long serverId) {
        List<Channel> channels = channelRepository.findAllByServerId((serverId));

        Server server = serverRepository.findById(serverId)
            .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        return channels.stream()
                .map(ChannelResponse::from)
                .toList();
    }

    @Transactional
    public ChannelResponse updateChannel(Long serverId, ChannelUpdateRequest request) {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_CHANNEL));

        if (!channel.getServer().getId().equals(serverId)) {
            throw new PreconditionFailException412(ErrorDefineCode.CHANNEL_NOT_IN_SERVER);
        }

        channel.setName(request.getChannelName());
        channel = channelRepository.save(channel);

        channelRedisPublisher.publish(ChannelCreatedOrUpdateMsgResponse.from(channel, serverId));

        return ChannelResponse.from(channel);
    }
}
