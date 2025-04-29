package dev.discord_server.domain.message.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.domain.message.dto.ChannelMsgRequest;
import dev.discord_server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebsocketController {
    private final MessageService messageService;

    @MessageMapping("/channel/{channelId}")
    public void handleMessage(@DestinationVariable Long channelId, ChannelMsgRequest request, Message<?> message) {
        Long userId = SecurityUtil.getCurrentUserId(message);
        messageService.sendMessage(channelId, userId, request.getContent());
    }
}
