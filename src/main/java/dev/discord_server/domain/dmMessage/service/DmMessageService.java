package dev.discord_server.domain.dmMessage.service;

import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.domain.dm.entity.Dm;
import dev.discord_server.domain.dm.repository.DmRepository;
import dev.discord_server.domain.dmMessage.dto.DmMessageResponse;
import dev.discord_server.domain.dmMessage.dto.SendMessage;
import dev.discord_server.domain.dmMessage.entity.DmMessage;
import dev.discord_server.domain.dmMessage.repository.DmMessageRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DmMessageService {
    private final DmMessageRepository dmMessageRepository;
    private final DmRepository dmRepository;
    private final UserRepository userRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;

    public List<DmMessageResponse> getMessages(Long dmId){
        return dmMessageRepository.findByDmIdOrderByCreatedAtAsc(dmId).stream()
                .map(msg -> new DmMessageResponse(
                        dmId,
                        msg.getUser().getNickname(),
                        msg.getUser().getImageUrl(),
                        msg.getContent(),
                        msg.getCreatedAt()
                ))
                .toList();
    }

    public SendMessage sendMessages(Long dmId,Long userId, String content){
        // TODO : 에러타입 맞춰야함.
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new RuntimeException("DM not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DmMessage message = DmMessage.builder()
                .id(snowflakeIdGenerator.generateId())
                .dm(dm)
                .user(user)
                .content(content)
                .build();

        DmMessage saved = dmMessageRepository.save(message);
        String formattedTime = saved.getCreatedAt()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new SendMessage(
                dmId,
                saved.getId(),
                user.getNickname(),
                user.getImageUrl(),
                saved.getContent(),
                formattedTime
        );
    }
}
