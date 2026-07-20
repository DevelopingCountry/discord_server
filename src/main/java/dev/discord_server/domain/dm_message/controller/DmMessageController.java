package dev.discord_server.domain.dm_message.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.dm_message.dto.DmMessageResponse;
import dev.discord_server.domain.dm_message.dto.UpdateDMMessageRequest;
import dev.discord_server.domain.dm_message.service.DmMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Tag(name = "DM 메시지 API", description = "DM 메시지 조회/수정/삭제 REST API. 실시간 전송/수신은 STOMP(/ws-chat)를 사용한다.")
@RestController
@RequestMapping("/dm")
@RequiredArgsConstructor
public class DmMessageController {
    private final DmMessageService dmMessageService;

    @Operation(summary = "DM 메시지 목록 조회", description = "해당 DM 대화의 전체 메시지 목록을 반환합니다.")
    @GetMapping("/{dmId}")
    public CommonResponse<List<DmMessageResponse>> getMessages(@Parameter(description = "DM 대화 ID", example = "123456789012345") @PathVariable Long dmId){
        List<DmMessageResponse> messages = dmMessageService.getMessages(dmId);
        return new CommonResponse<>(true, HttpStatus.OK, "모든 메세지가 반환되었습니다.", messages);
    }

    @Operation(summary = "DM 메시지 수정", description = "본인이 작성한 DM 메시지의 내용을 수정합니다.")
    @PatchMapping("/{dmId}/message/{messageId}")
    public CommonResponse<Void> updateMessage(@Parameter(description = "DM 대화 ID", example = "123456789012345") @PathVariable Long dmId,
                                              @Parameter(description = "메시지 ID", example = "123456789012345") @PathVariable Long messageId,
                                              @RequestBody UpdateDMMessageRequest request) {
        Long userId = SecurityUtil.getCurrentUserId();
        dmMessageService.updateMessage(dmId, messageId, userId, request.getContent());


        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 수정되었습니다.",null);
    }

    @Operation(summary = "DM 메시지 삭제", description = "본인이 작성한 DM 메시지를 삭제합니다.")
    @DeleteMapping("/{dmId}/message/{messageId}")
    public CommonResponse<Void> deleteMessage(@Parameter(description = "DM 대화 ID", example = "123456789012345") @PathVariable Long dmId,
                                              @Parameter(description = "메시지 ID", example = "123456789012345") @PathVariable Long messageId) {
        Long userId = SecurityUtil.getCurrentUserId();
        dmMessageService.deleteMessage(dmId, messageId, userId);
        return new CommonResponse<>(true, HttpStatus.OK, "메시지가 삭제되었습니다.",null);
    }

}
