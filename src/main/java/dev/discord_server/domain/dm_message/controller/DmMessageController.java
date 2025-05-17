package dev.discord_server.domain.dm_message.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.dm_message.dto.DmMessageResponse;
import dev.discord_server.domain.dm_message.dto.UpdateDMMessageRequest;
import dev.discord_server.domain.dm_message.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/dm")
@RequiredArgsConstructor
public class DmMessageController {
    private final DmMessageService dmMessageService;

    @GetMapping("/{dmId}")
    public CommonResponse<List<DmMessageResponse>> getMessages(@PathVariable Long dmId){
        List<DmMessageResponse> messages = dmMessageService.getMessages(dmId);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 메세지가 반환되었습니다.", messages);
    }

    @PatchMapping("/{dmId}/message/{messageId}")
    public CommonResponse<Void> updateMessage(@PathVariable Long dmId,
                                              @PathVariable Long messageId,
                                              @RequestBody UpdateDMMessageRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        dmMessageService.updateMessage(dmId, messageId, userId, request.getContent());


        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 수정되었습니다.",null);
    }

    @DeleteMapping("/{dmId}/message/{messageId}")
    public CommonResponse<Void> deleteMessage(@PathVariable Long dmId,
                                              @PathVariable Long messageId) {
        Long userId = SecurityUtil.getCurrentUserId();
        dmMessageService.deleteMessage(dmId, messageId, userId);
        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 삭제되었습니다.",null);
    }

}
