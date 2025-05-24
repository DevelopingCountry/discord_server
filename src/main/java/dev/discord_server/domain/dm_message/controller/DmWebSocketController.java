package dev.discord_server.domain.dm_message.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.config.redis.DmSessionTracker;
import dev.discord_server.domain.dm_message.dto.DmMsgRequest;
import dev.discord_server.domain.dm_message.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DmWebSocketController {
    private final DmMessageService dmMessageService;
    private final DmSessionTracker dmSessionTracker;

    @MessageMapping("/dm/{dmId}")
    public void handleMessage(@DestinationVariable Long dmId, DmMsgRequest request, Message<?> message) {
        Long userId = SecurityUtil.getCurrentUserId(message);
        dmSessionTracker.enterDm(dmId.toString(), userId);

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        accessor.getSessionAttributes().put("dmId", dmId.toString());
        accessor.getSessionAttributes().put("userId", userId);
        dmMessageService.sendMessage(dmId, userId, request.getContent());
    }


}