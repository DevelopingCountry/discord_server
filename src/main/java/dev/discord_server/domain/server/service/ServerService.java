package dev.discord_server.domain.server.service;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.server.dto.ServerImageUpdateRequest;
import dev.discord_server.domain.server.dto.ServerInviteRequest;
import dev.discord_server.domain.server.dto.ServerRequest;
import dev.discord_server.domain.server.dto.ServerResponse;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.server.repository.ServerRepository;
import dev.discord_server.domain.serverUser.entity.ServerUser;
import dev.discord_server.domain.serverUser.entity.ServerUserRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.List;
import java.util.UUID;

/**
 * 예외처리 사용 방법
 * config -> custom -> exception에 많이 쓰이는 에러 종류들 작성해놓음 ex) noSuchElementFoundException404
 * ErrorDefindCode에 본인이 사용하고 싶은 에러 작성하고 사용
 */


@RequiredArgsConstructor
@Service
public class ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ServerUserRepository serverUserRepository;

    public List<ServerResponse> findServers(UUID userId) {
        List<Server> all = serverRepository.findAll();
        if (all.isEmpty()) {
            throw new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER);
        }

        return all.stream()
                .map(server -> {
                    boolean alarm = server.getServerUsers().stream()
                            .filter(su -> su.getUser().getId().equals(userId))
                            .findFirst()
                            .map(ServerUser::isAlarm) // 또는 getAlarm()
                            .orElse(false);

                    return ServerResponse.toResponseDto(server, alarm);
                })
                .toList();
    }


    public UUID addServer(UUID userId, ServerRequest serverRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Server server = Server.createServer(
                serverRequest.getServerName(),
                serverRequest.getImage(),
                user
                );

        serverRepository.save(server);
        return server.getId();
    }

    public void updateServerName(UUID userId, UUID serverId, String newName) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        if (!server.getHost().getId().equals(userId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }
        server.setName(newName);
        serverRepository.save(server);
    }

    public void updateServerImage(UUID serverId, ServerImageUpdateRequest request) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        server.setName(request.getServerName());
        server.setImage(request.getImage());
        serverRepository.save(server);
    }


    public void inviteUser(UUID serverId, ServerInviteRequest request) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        UUID currentUserId = SecurityUtil.getCurrentUserId();

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        User guest = userRepository.findById(request.getGuestId())
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        ServerUser invited = ServerUser.builder()
                .server(server)
                .user(guest)
                .alarm(true)
                .build();

        serverUserRepository.save(invited);
    }



}
