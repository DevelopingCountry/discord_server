package dev.discord_server.domain.dmMessage.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.domain.dmMessage.dto.DmMsgRequest;
import dev.discord_server.domain.dmMessage.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DmWebSocketController {
    private final DmMessageService dmMessageService;

    @MessageMapping("/dm/{dmId}")
    public void handleMessage(@DestinationVariable Long dmId, DmMsgRequest request, Message<?> message) {
        Long userId = SecurityUtil.getCurrentUserId(message);
        dmMessageService.sendMessage(dmId, userId, request.getContent());
    }


}