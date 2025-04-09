package dev.discord_server.domain.server.service;

import dev.discord_server.common.response.ErrorDefineCode;
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
            throw new NoSuchElementFoundException404(ErrorDefineCode.EXAMPLE_OCCURE_ERROR);
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
                .orElseThrow(() -> new EntityNotFoundException("해당 유저가 존재하지 않습니다."));

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
                .orElseThrow(() -> new EntityNotFoundException("해당 서버가 존재하지 않습니다."));

        if (!server.getHost().equals(userId)) {
            throw new AccessDeniedException("해당 서버를 수정할 권한이 없습니다.");
        }
        server.setName(newName);
        serverRepository.save(server);
    }

    public void updateServerImage(UUID serverId, ServerImageUpdateRequest request) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new EntityNotFoundException("해당 서버가 존재하지 않습니다."));

        if (!server.getHost().getId().equals(request.getUserId())) {
            throw new AccessDeniedException("해당 서버를 수정할 권한이 없습니다.");
        }

        server.setImage(request.getImage());
        serverRepository.save(server);
    }


    public void inviteUser(UUID serverId, ServerInviteRequest request) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new EntityNotFoundException("서버를 찾을 수 없습니다."));

        if (!server.getHost().getId().equals(request.getHostId())) {
            throw new AccessDeniedException("해당 서버에 초대할 권한이 없습니다.");
        }

        User guest = userRepository.findById(request.getGuestId())
                .orElseThrow(() -> new EntityNotFoundException("초대할 유저가 존재하지 않습니다."));

        ServerUser invited = ServerUser.builder()
                .server(server)
                .user(guest)
                .alarm(true)
                .build();

        serverUserRepository.save(invited);
    }



}
