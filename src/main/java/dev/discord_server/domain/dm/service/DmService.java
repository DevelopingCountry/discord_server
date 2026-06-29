package dev.discord_server.domain.dm.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.dm.dto.DmAddResponse;
import dev.discord_server.domain.dm.dto.DmUserResponse;
import dev.discord_server.domain.dm.dto.DmVisibleRequest;
import dev.discord_server.domain.dm.dto.DmVisibleResponse;
import dev.discord_server.domain.dm.entity.Dm;
import dev.discord_server.domain.dm.repository.DmRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DmService {
    private final DmRepository dmRepository;
    private final UserRepository userRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public List<DmUserResponse> findDmUsers(Long currentId) {
        List<Dm> rooms = dmRepository.findByIsVisibleTrueAndUser1IdOrUser2Id(currentId,currentId);

        return rooms.stream()
                .map(room -> {
                    User target = room.getUser1().getId().equals(currentId)
                            ? room.getUser2()
                            : room.getUser1();
                    return new DmUserResponse(room.getId().toString(),target.getId().toString(),  target.getImageUrl(),target.getNickname());
                })
                .toList();
    }

    public DmAddResponse findOrCreateDm(Long userId, Long targetUserId) {
        if (userId.equals(targetUserId)) {
            throw new ForbiddenException403(ErrorDefineCode.SELF_DM_NOT_ALLOWED);
        }

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Optional<Dm> existing = dmRepository.findByUser1IdAndUser2IdOrUser2IdAndUser1Id(
                userId, targetUserId, userId, targetUserId
        );

        if (existing.isPresent()) {
            Dm dm = existing.get();
            if (!dm.isVisible()) {
                dm.setVisible(true);  // 숨겼던 DM 재개시 다시 표시
                dmRepository.save(dm);
            }
            return new DmAddResponse(dm.getId().toString(), targetUser.getId().toString(), targetUser.getImageUrl(), targetUser.getNickname());
        }

        Long dmId = dmRepository.save(Dm.builder()
                .id(snowflakeIdGenerator.generateId())
                .isVisible(true)
                .user1(currentUser)
                .user2(targetUser)
                .build()).getId();

        return new DmAddResponse(dmId.toString(), targetUser.getId().toString(), targetUser.getImageUrl(), targetUser.getNickname());
    }


    @Transactional
    public DmVisibleResponse nonVisibleDm(DmVisibleRequest request) {
        Dm dm = dmRepository.findById(Long.valueOf(request.getDmId()))
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.NOT_FOUND_DM));

        dm.setVisible(false); // 👈 isVisible을 false로 변경

        return DmVisibleResponse.builder()
                .id(dm.getId())
                .isVisible(dm.isVisible())
                .build();
    }

}
