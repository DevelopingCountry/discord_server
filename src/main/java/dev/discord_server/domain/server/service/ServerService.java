package dev.discord_server.domain.server.service;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.AlreadyExistElementException409;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.exception.custom.exception.PreconditionFailException412;
import dev.discord_server.domain.friend.entity.Friend;
import dev.discord_server.domain.friend.repository.FriendRepository;
import dev.discord_server.domain.server.dto.*;
import dev.discord_server.domain.server.entity.Server;
import dev.discord_server.domain.server.entity.ServerInvite;
import dev.discord_server.domain.server.repository.ServerInviteRepository;
import dev.discord_server.domain.server.repository.ServerRepository;
import dev.discord_server.domain.server.entity.Enum.InviteStatus;
import dev.discord_server.domain.serverUser.entity.ServerUser;
import dev.discord_server.domain.serverUser.entity.ServerUserRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.*;


import java.util.stream.Collectors;

/**
 * 예외처리 사용 방법
 * config -> custom -> exception에 많이 쓰이는 에러 종류들 작성해놓음 ex) noSuchElementFoundException404
 * ErrorDefindCode에 본인이 사용하고 싶은 에러 작성하고 사용
 */


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ServerService {

    private final ServerRepository serverRepository;
    private final UserRepository userRepository;
    private final ServerUserRepository serverUserRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final ServerInviteRepository serverInviteRepository;
    private final NotificationService notificationService;
    private final FriendRepository friendRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String ONLINE_KEY = "online_users";

    public List<ServerResponse> findServers() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        List<Server> servers = serverRepository.findByServerUsers_User_Id(currentUserId);

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
    public ServerResponse addServer(ServerCreateRequest serverCreateRequest) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        if(serverRepository.existsByServerName(serverCreateRequest.getServerName())){
            throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_SERVERNAME);
        }

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

        return new ServerResponse(
                String.valueOf(server.getId()),
                server.getServerName(),
                server.getImage(),
                createUser.isAlarm(),
                String.valueOf(server.getHost().getId())
        );
    }

    @Transactional
    public ServerUpdateResponse updateServerInfo(Long serverId, ServerInfoUpdateRequest serverInfoUpdateRequest) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));

        if (!server.getHost().getId().equals(currentUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.AUTHORIZATION_FAIL);
        }
        if(serverRepository.existsByServerName(serverInfoUpdateRequest.getServerName())){
            throw new AlreadyExistElementException409(ErrorDefineCode.DUPLICATE_SERVERNAME);
        }
        server.setServerName(serverInfoUpdateRequest.getServerName());
        server.setImage(serverInfoUpdateRequest.getImageUrl());

        serverUserRepository.findByServerId(serverId).forEach(su ->
                notificationService.sendServerUpdatedNotification(
                        su.getUser().getId(), serverId, server.getServerName(), server.getImage())
        );

        return new ServerUpdateResponse(
                String.valueOf(serverId),
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

        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        User guest = userRepository.findById(Long.valueOf(request.getGuestId()))
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        boolean isDuplicate = serverInviteRepository.existsByServerIdAndToUserIdAndStatus(
                serverId, guest.getId(), InviteStatus.PENDING);

        if (isDuplicate) {
            throw new AlreadyExistElementException409(ErrorDefineCode.EXIST_SERVER_INVITE);
        }


        ServerInvite invite = ServerInvite.builder()
                .id(snowflakeIdGenerator.generateId())
                .server(server)
                .fromUser(user)
                .toUser(guest)
                .status(InviteStatus.PENDING)
                .build();

        serverInviteRepository.save(invite);


        notificationService.sendInviteNotification(
                invite.getId(),
                server.getImage(),
                guest.getId(),
                server.getServerName(),
                user.getNickname(),
                user.getImageUrl(),
                serverId
        );
    }




    @Transactional
    public void acceptInvite(Long inviteId) {
        ServerInvite invite = serverInviteRepository.findById(inviteId)
                .orElseThrow(()-> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER)); //수정해야함 초대가 없는걸로

        if (invite.getStatus() != InviteStatus.PENDING)
            throw new IllegalStateException("이미 처리된 초대입니다."); // Accept면 이미 받은거 Declare면 거부된 요청

        ServerUser serverUser = ServerUser.builder()
                .id(snowflakeIdGenerator.generateId())
                .server(invite.getServer())
                .user(invite.getToUser())
                .alarm(true)
                .build();

        serverUserRepository.save(serverUser);
        invite.setStatus(InviteStatus.ACCEPTED);

        notificationService.sendServerInviteAcceptedNotification(
                invite.getFromUser().getId(), invite.getServer().getId());
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

        return new ServerAlarmUpdateResponse(String.valueOf(serverId), request.isAlarm());
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


    public List<FriendInvitedListDto> getInvitedUsers(Long serverId) {
        Long currentUserId = SecurityUtil.getCurrentUserId();

        serverRepository.findById(serverId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_SERVER));
        //1. 친구 조회
        List<Friend> acceptedFriends = friendRepository.findAcceptedFriends(currentUserId);
        log.info("친구를 조회합니다.");
        log.info(String.valueOf(acceptedFriends));
        //2. 현 서버에 소속 되어있나?
        List<Long>[] list = new ArrayList[2];

        list[0] = new ArrayList<>();
        list[1] = new ArrayList<>();
        for(Friend friend : acceptedFriends){
            Long friendUserId = friend.getFriendId(currentUserId);
            // 3. 초대를 한상태인가?
            if(!serverUserRepository.existsByServerIdAndUserId(serverId,friendUserId)){
                //3-1. yes
                if(serverInviteRepository.existsByServerIdAndToUserIdAndStatus(serverId,friendUserId,InviteStatus.PENDING)){
                    list[0].add(friendUserId);
                }
                //3-2. no
                else{
                    list[1].add(friendUserId);
                }
            }
        }
        log.info("초대한 사람 id");
        log.info(String.valueOf(list[0].size()));
        log.info("초대안 사람 id");
        log.info(String.valueOf(list[1].size()));
        //4. 초대를 한사람들과 안한사람들을 구분짓기
        List<FriendInvitedListDto> inviteY = userRepository.findInviteY(list[0]);
        log.info("초대한 사람 조회.");
        log.info(String.valueOf(inviteY));
        List<FriendInvitedListDto> inviteN = userRepository.findInviteN(list[1]);
        log.info("초대안 사람 조회.");
        log.info(String.valueOf(inviteY));
        List<FriendInvitedListDto> resultt = new ArrayList<>(inviteY);
        log.info(String.valueOf(resultt));
        resultt.addAll(inviteN);


        return resultt;

//        Set<Long> invitedIds = serverInviteRepository.findByServerId(serverId).stream()
//                .filter(invite -> invite.getStatus() == InviteStatus.PENDING)
//                .map(invite -> invite.getToUser().getId())
//                .collect(Collectors.toSet());
//
//        return acceptedFriends.stream()
//                .map(friend -> {
//                    User target = friend.getFromUser().getId().equals(currentUserId)
//                            ? friend.getToUser() : friend.getFromUser();
//                    return new InvitableFriendResponse(
//                            target.getId().toString(),
//                            target.getNickname(),
//                            target.getImageUrl(),
//                            invitedIds.contains(target.getId())
//                    );
//                })
//                .toList();
    }

    public List<ServerMemberResponse> getServerMembers(Long serverId) {
        Set<String> onlineUsers = redisTemplate.opsForSet().members(ONLINE_KEY);

        return serverUserRepository.findByServerId(serverId).stream()
                .map(su -> {
                    User user = su.getUser();
                    boolean isOnline = onlineUsers != null && onlineUsers.contains(String.valueOf(user.getId()));
                    return ServerMemberResponse.builder()
                            .userId(String.valueOf(user.getId()))
                            .nickname(user.getNickname())
                            .imageUrl(user.getImageUrl())
                            .online(isOnline)
                            .build();
                })
                .collect(Collectors.toList());
    }
}
