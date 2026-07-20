package dev.discord_server.domain.message.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.message.dto.MessageResponse;
import dev.discord_server.domain.message.dto.UpdateMessageRequest;
import dev.discord_server.domain.message.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채널 메시지 API", description = "채널 텍스트 메시지 조회/수정/삭제 REST API. 실시간 전송/수신은 STOMP(/ws-chat)를 사용한다.")
@RestController
@RequestMapping("/channel")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @Operation(summary = "채널 메시지 목록 조회", description = "해당 채널의 전체 메시지 목록을 반환합니다.")
    @GetMapping("/{channelId}/messages")
    public CommonResponse<List<MessageResponse>> getMessages(@Parameter(description = "채널 ID", example = "123456789012345") @PathVariable Long channelId) {
        List<MessageResponse> messages = messageService.getMessages(channelId);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 메세지가 반환되었습니다.", messages);
    }

    @Operation(summary = "채널 메시지 수정", description = "본인이 작성한 채널 메시지의 내용을 수정합니다.")
    @PatchMapping("/{channelId}/message/{messageId}")
    public CommonResponse<Void> updateMessage(@Parameter(description = "채널 ID", example = "123456789012345") @PathVariable Long channelId,
                                              @Parameter(description = "메시지 ID", example = "123456789012345") @PathVariable Long messageId,
                                              @RequestBody UpdateMessageRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.updateMessage(channelId, messageId, userId, request.getContent());
        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 수정되었습니다.", null);
    }

    @Operation(summary = "채널 메시지 삭제", description = "본인이 작성한 채널 메시지를 삭제합니다.")
    @DeleteMapping("/{channelId}/message/{messageId}")
    public CommonResponse<Void> deleteMessage(@Parameter(description = "채널 ID", example = "123456789012345") @PathVariable Long channelId,
                                              @Parameter(description = "메시지 ID", example = "123456789012345") @PathVariable Long messageId) {
        Long userId = SecurityUtil.getCurrentUserId();
        messageService.deleteMessage(channelId, messageId, userId);
        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 삭제되었습니다.", null);
    }
}
