package dev.discord_server.domain.message.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.message.dto.MessageResponse;
import dev.discord_server.domain.message.dto.UpdateMessageRequest;
import dev.discord_server.domain.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{channelId}/messages")
    public CommonResponse<List<MessageResponse>> getMessages(@PathVariable Long channelId) {
        List<MessageResponse> messages = messageService.getMessages(channelId);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 메세지가 반환되었습니다.", messages);
    }

    @PatchMapping("/{channelId}/message/{messageId}")
    public CommonResponse<Void> updateMessage(@PathVariable Long channelId,
                                              @PathVariable Long messageId,
                                              @RequestBody UpdateMessageRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.updateMessage(channelId, messageId, userId, request.getContent());
        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 수정되었습니다.", null);
    }

    @DeleteMapping("/{channelId}/message/{messageId}")
    public CommonResponse<Void> deleteMessage(@PathVariable Long channelId,
                                              @PathVariable Long messageId) {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.deleteMessage(channelId, messageId, userId);
        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 삭제되었습니다.", null);
    }
}
