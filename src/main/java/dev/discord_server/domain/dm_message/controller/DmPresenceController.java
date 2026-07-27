package dev.discord_server.domain.dm_message.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.config.redis.DmSessionTracker;
import dev.discord_server.domain.dm.service.DmService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DmPresenceController {

    private final DmSessionTracker dmSessionTracker;
    private final DmService dmService;

    @MessageMapping("/dm/{dmId}/enter")
    public void enterDm(@DestinationVariable String dmId, Message<?> message) {
        Long userId = SecurityUtil.getCurrentUserId(message);
        dmSessionTracker.enterDm(dmId, userId);
        dmService.markRead(Long.valueOf(dmId), userId);

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        accessor.getSessionAttributes().put("dmId", dmId);
        accessor.getSessionAttributes().put("userId", userId);
    }

    @MessageMapping("/dm/{dmId}/leave")
    public void leaveDm(@DestinationVariable String dmId, Message<?> message) {
        Long userId = SecurityUtil.getCurrentUserId(message);
        dmSessionTracker.leaveDm(dmId, userId);
    }
}
