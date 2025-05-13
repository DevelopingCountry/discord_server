package dev.discord_server.domain.message.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.discord_server.common.response.ErrorDefineCode;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.config.exception.custom.exception.ForbiddenException403;
import dev.discord_server.config.exception.custom.exception.NoSuchElementFoundException404;
import dev.discord_server.config.redis.RedisPublisher;
import dev.discord_server.domain.channel.dto.ChannelWebSocketMessage;
import dev.discord_server.domain.channel.entity.Channel;
import dev.discord_server.domain.channel.entity.ChannelRepository;
import dev.discord_server.domain.message.dto.ChannelChatPayload;
import dev.discord_server.domain.message.dto.MessageResponse;
import dev.discord_server.domain.message.entity.Message;
import dev.discord_server.domain.message.repository.MessageRepository;
import dev.discord_server.domain.server.repository.ServerRepository;
import dev.discord_server.domain.serverUser.entity.ServerUserRepository;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final ServerUserRepository serverUserRepository;
    private final UserRepository userRepository;
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final RedisPublisher redisPublisher;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<MessageResponse> getMessages(Long channelId) {
        return messageRepository.findByChannelIdOrderByCreatedAtAsc(channelId).stream()
                .map(msg -> new MessageResponse(
                        channelId.toString(),
                        msg.getId().toString(),
                        msg.getUser().getId().toString(),
                        msg.getUser().getNickname(),
                        msg.getUser().getImageUrl(),
                        msg.getContent(),
                        msg.getCreatedAt()
                ))
                .toList();
    }

    public void sendMessage(Long channelId, Long senderId, String content) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_CHANNEL));

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        Message saved = messageRepository.save(Message.builder()
                .id(snowflakeIdGenerator.generateId())
                .channel(channel)
                .user(sender)
                .content(content)
                .build());

        var socketPayload = new ChannelChatPayload(
                channel.getId().toString(),
                saved.getId().toString(),
                sender.getNickname(),
                sender.getImageUrl(),
                saved.getContent(),
                saved.getCreatedAt().toString(),
                sender.getId().toString()
        );

        publish(socketPayload, "SEND");
    }

    public void updateMessage(Long channelId, Long messageId, Long userId, String content) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        if (!message.getChannel().getId().equals(channelId)) {
            throw new ForbiddenException403(ErrorDefineCode.CHANNEL_NOT_IN_SERVER);
        }

        if (!message.getUser().getId().equals(userId)) {
            throw new ForbiddenException403(ErrorDefineCode.UNAUTHORIZED_MESSAGE_ACCESS);
        }

        message.updateContent(content);

        var socketPayload = new ChannelChatPayload(
                message.getChannel().getId().toString(),
                message.getId().toString(),
                message.getUser().getNickname(),
                message.getUser().getImageUrl(),
                message.getContent(),
                message.getCreatedAt().toString(),
                message.getUser().getId().toString()
        );

        publish(socketPayload, "UPDATE");
    }

    public void deleteMessage(Long channelId, Long messageId, Long userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementFoundException404(ErrorDefineCode.EMPTY_USER));

        if (!message.getChannel().getId().equals(channelId)) {
            throw new ForbiddenException403(ErrorDefineCode.CHANNEL_NOT_IN_SERVER);
        }

        if (!message.getUser().getId().equals(userId)) {
            throw new ForbiddenException403(ErrorDefineCode.UNAUTHORIZED_MESSAGE_ACCESS);
        }

        messageRepository.delete(message);

        var socketPayload = new ChannelChatPayload(
                message.getChannel().getId().toString(),
                message.getId().toString(),
                null, null, null,
                message.getCreatedAt().toString(),
                message.getUser().getId().toString()
        );

        publish(socketPayload, "DELETE");
    }

    private void publish(ChannelChatPayload message, String type) {
        try {
            log.info("📤 Redis 전송 [{}] - messageId: {}, content: {}", type, message.messageId(), message.content());
            String payload = objectMapper.writeValueAsString(new ChannelWebSocketMessage(type, message));
            redisPublisher.publishMessage(payload);
        } catch (Exception e) {
            log.error("❌ Redis 메시지 전송 실패", e);
        }
    }
}
