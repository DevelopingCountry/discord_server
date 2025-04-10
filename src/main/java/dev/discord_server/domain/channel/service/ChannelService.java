package dev.discord_server.domain.channel.service;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.channel.dto.ChannelCreateRequest;
import dev.discord_server.domain.channel.dto.ChannelDeleteRequest;
import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.domain.channel.entity.ChannelRepository;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.server.repository.ServerRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChannelService {
    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;


    public void createChannel(UUID serverId, ChannelCreateRequest request) {
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
    }

    public void deleteChannel(UUID serverId, UUID channelId) {
        UUID hostId = SecurityUtil.getCurrentUserId();
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        if (!server.getHost().getId().equals(hostId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_CHANNEL));

        if (!channel.getServer().getId().equals(serverId)) {
            throw new ForbiddenException403(ErrorDefineCode.CHANNEL_NOT_IN_SERVER);
        }

        channelRepository.delete(channel);
    }

}
