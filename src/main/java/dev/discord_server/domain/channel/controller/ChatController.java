package dev.discord_server.domain.channel.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.domain.channel.dto.ChannelMsgRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {

    @MessageMapping("/channel/{channelId}")
    public void sendChannelMessage(@DestinationVariable String channelId, ChannelMsgRequest request, Message<?> message){
        Long userId = SecurityUtil.getCurrentUserId(message);

    }
}
