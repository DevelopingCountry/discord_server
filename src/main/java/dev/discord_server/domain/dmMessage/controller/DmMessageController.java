package dev.discord_server.domain.dmMessage.controller;

import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.dmMessage.dto.DmMessageResponse;
import dev.discord_server.domain.dmMessage.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

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
}
