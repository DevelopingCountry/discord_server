package dev.discord_server.domain.server.service;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.exception.custom.exception.PreconditionFailException412;
import dev.discord_server.domain.server.dto.*;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.server.repository.ServerRepository;
import dev.discord_server.domain.serverUser.entity.ServerUser;
import dev.discord_server.domain.serverUser.entity.ServerUserRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 예외처리 사용 방법
 * config -> custom -> exception에 많이 쓰이는 에러 종류들 작성해놓음 ex) noSuchElementFoundException404
 * ErrorDefindCode에 본인이 사용하고 싶은 에러 작성하고 사용
 */


@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ServerUserRepository serverUserRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public List<ServerResponse> findServers() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<Server> servers = serverRepository.findByServerUsers_User_Id(currentUserId);

        if (servers.isEmpty()) {
            throw new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER);
        }

        return servers.stream()
                .map(server -> {
                    boolean alarm = server.getServerUsers().stream()
                            .filter(su -> su.getUser().getId().equals(currentUserId))
                            .findFirst()
                            .map(ServerUser::isAlarm)
                            .orElse(false);

                    return ServerResponse.toResponseDto(server, alarm);
                })
                .toList();
    }



    @Transactional
    public ServerCreateOrUpdateResponse addServer(ServerCreateRequest serverCreateRequest) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));


        Server server = Server.createServer(
                snowflakeIdGenerator.generateId(),
                serverCreateRequest.getServerName(),
                serverCreateRequest.getImageUrl(),
                user
                );


        serverRepository.save(server);
        serverRepository.flush();

        ServerUser createUser = ServerUser.builder()
                .id(snowflakeIdGenerator.generateId())
                .server(server)
                .user(user)
                .alarm(true)
                .build();

        serverUserRepository.save(createUser);

        return new ServerCreateOrUpdateResponse(
                server.getId(),
                server.getImage(),
                server.getServerName()
        );
    }

    @Transactional
    public ServerCreateOrUpdateResponse updateServerInfo(Long serverId, ServerInfoUpdateRequest serverInfoUpdateRequest) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }
        server.setServerName(serverInfoUpdateRequest.getServerName());
        server.setImage(serverInfoUpdateRequest.getImageUrl());

        return new ServerCreateOrUpdateResponse(
                serverId,
                server.getImage(),
                server.getServerName()
        );
    }



    @Transactional
    public void inviteUser(Long serverId, ServerInviteRequest request) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        Long currentUserId = SecurityUtil.getCurrentUserId();

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        User guest = userRepository.findById(request.getGuestId())
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));


        ServerUser invited = ServerUser.builder()
                .id(snowflakeIdGenerator.generateId())
                .server(server)
                .user(guest)
                .alarm(true)
                .build();

        serverUserRepository.save(invited);
    }



    @Transactional
    public ServerAlarmUpdateResponse updateAlarm(Long serverId, ServerAlarmUpdateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        System.out.println(currentUserId);

        ServerUser serverUser = server.getServerUsers().stream()
                .filter(su -> su.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL));

        serverUser.setAlarm(request.isAlarm());

        return new ServerAlarmUpdateResponse(serverId, request.isAlarm());
    }



    @Transactional
    public void exitServer(Long serverId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        if(server.getHost().getId().equals(currentUserId)) {
            throw new PreconditionFailException412(ErrorDefineCode.CANT_EXIT_HOST_SERVER);
        }


        ServerUser serverUser = serverUserRepository.findByServerIdAndUserId(serverId, currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.NOT_JOINED_SERVER));

        serverUserRepository.delete(serverUser);
    }



    @Transactional
    public void deleteServer(Long serverId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }

        // 관련 엔티티들(예: ServerUser, Channel 등) cascade를 통해 같이 삭제
        serverRepository.delete(server);
    }






}
