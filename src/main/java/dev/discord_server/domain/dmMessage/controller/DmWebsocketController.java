package dev.discord_server.domain.dmMessage.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.dmMessage.dto.DmMsgRequest;
import dev.discord_server.domain.dmMessage.dto.SendMessage;
import dev.discord_server.domain.dmMessage.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class DmWebsocketController {

    private final DmMessageService dmMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/dm/{dmId}")
    public void handleMessage(@DestinationVariable Long dmId, DmMsgRequest dmMsgRequest, Message<?> message) {
        Long userId = SecurityUtil.getCurrentUserId(message);
        SendMessage response = dmMessageService.sendMessages(dmId, userId, dmMsgRequest.getContent());

        CommonResponse<SendMessage> wrapped = new CommonResponse<>(
                true,
                HttpStatus.OK,
                "메시지 전송 성공",
                response
        );

        simpMessagingTemplate.convertAndSend("/topic/dm/" + dmId, wrapped);

    }
}
