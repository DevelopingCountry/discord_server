package dev.discord_server.domain.dmMessage.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.redis.RedisPublisher;
import dev.discord_server.domain.dm.entity.Dm;
import dev.discord_server.domain.dm.repository.DmRepository;
import dev.discord_server.domain.dmMessage.dto.ChatPayload;
import dev.discord_server.domain.dmMessage.dto.DmMessageResponse;
import dev.discord_server.domain.dmMessage.dto.WebSocketMessage;
import dev.discord_server.domain.dmMessage.entity.DmMessage;
import dev.discord_server.domain.dmMessage.repository.DmMessageRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DmMessageService {
    private final DmMessageRepository dmMessageRepository;
    private final DmRepository dmRepository;
    private final UserRepository userRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private boolean isParticipant(Dm dm, Long userId) {
        return dm.getUser1().getId().equals(userId) || dm.getUser2().getId().equals(userId);
    }

    public List<DmMessageResponse> getMessages(Long dmId){
        return dmMessageRepository.findByDmIdOrderByCreatedAtAsc(dmId).stream()
                .map(msg -> new DmMessageResponse(
                        dmId.toString(),
                        msg.getId().toString(),
                        msg.getUser().getId().toString(),
                        msg.getUser().getNickname(),
                        msg.getUser().getImageUrl(),
                        msg.getContent(),
                        msg.getCreatedAt()
                ))
                .toList();
    }

    public void sendMessage(Long dmId, Long senderId, String content) {
        Dm dm = dmRepository.findById(dmId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.NOT_FOUND_DM));

        if (!isParticipant(dm, senderId)) {
            throw new NoSuchElementFoundException404(ErrorDefineCode.NOT_PARTICIPANT_DM);
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        DmMessage saved = dmMessageRepository.save(DmMessage.builder()
                .id(snowflakeIdGenerator.generateId())
                .dm(dm)
                .user(sender)
                .content(content)
                .build());

        log.info("✅ 저장된 메시지 ID: {}", saved.getId());

        var socketPayload = new ChatPayload(
                dm.getId().toString(),
                saved.getId().toString(),
                sender.getNickname(),
                sender.getImageUrl(),
                saved.getContent(),
                saved.getCreatedAt().toString()
        );

        publish(socketPayload, "SEND");
    }

    public void updateMessage(Long dmId, Long messageId, Long userId, String content) {
        DmMessage message = dmMessageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        if (!message.getDm().getId().equals(dmId)) {
            throw new ForbiddenException403(ErrorDefineCode.DM_MESSAGE_MISMATCH);
        }

        if (!message.getUser().getId().equals(userId)) {
            throw new ForbiddenException403(ErrorDefineCode.UNAUTHORIZED_MESSAGE_ACCESS);
        }

        message.updateContent(content);

        var socketPayload = new ChatPayload(
                message.getDm().getId().toString(),
                message.getId().toString(),
                message.getUser().getNickname(),
                message.getUser().getImageUrl(),
                message.getContent(),
                message.getCreatedAt().toString()
        );

        publish(socketPayload, "UPDATE");
    }

    public void deleteMessage(Long dmId, Long messageId, Long userId) {
        DmMessage message = dmMessageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.NOT_FOUND_DM));

        if (!message.getDm().getId().equals(dmId)) {
            throw new ForbiddenException403(ErrorDefineCode.DM_MESSAGE_MISMATCH);
        }

        if (!message.getUser().getId().equals(userId)) {
            throw new ForbiddenException403(ErrorDefineCode.UNAUTHORIZED_MESSAGE_ACCESS);
        }

        dmMessageRepository.delete(message);

        var socketPayload = new ChatPayload(
                message.getDm().getId().toString(),
                message.getId().toString(),
                null,null,null,
                message.getCreatedAt().toString()
        );

        publish(socketPayload, "DELETE");
    }

    private void publish(ChatPayload message, String type) {
        try {
            log.info("📤 Redis 전송 [{}] - messageId: {}, content: {}", type, message.messageId(), message.content());
            String payload = objectMapper.writeValueAsString(new WebSocketMessage(type, message));
            redisPublisher.publishDm(payload);
        } catch (Exception e) {
            log.error("❌ Redis 메시지 전송 실패", e);
        }
    }


}
