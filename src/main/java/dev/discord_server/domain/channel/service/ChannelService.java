package dev.discord_server.domain.channel.service;

import dev.discord_server.auth.util.SecurityUtil;
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
                .orElseThrow(() -> new EntityNotFoundException("서버를 찾을 수 없습니다."));

        UUID currentUserId = SecurityUtil.getCurrentUserId();

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new AccessDeniedException("채널을 생성할 권한이 없습니다.");
        }

        User creator = userRepository.findById(currentUserId)
                .orElseThrow(() -> new EntityNotFoundException("유저를 찾을 수 없습니다."));

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
                .orElseThrow(() -> new EntityNotFoundException("서버를 찾을 수 없습니다."));

        if (!server.getHost().getId().equals(hostId)) {
            throw new AccessDeniedException("채널 삭제 권한이 없습니다.");
        }

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("채널을 찾을 수 없습니다."));

        if (!channel.getServer().getId().equals(serverId)) {
            throw new IllegalArgumentException("해당 서버에 속한 채널이 아닙니다.");
        }

        channelRepository.delete(channel);
    }

}
