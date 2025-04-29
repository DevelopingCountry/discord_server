package dev.discord_server.domain.dm.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.dm.dto.DmAddResponse;
import dev.discord_server.domain.dm.dto.DmUserResponse;
import dev.discord_server.domain.dm.dto.DmVisibleRequest;
import dev.discord_server.domain.dm.dto.DmVisibleResponse;
import dev.discord_server.domain.dm.service.DmService;
import dev.discord_server.domain.dmMessage.dto.DmStartRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dm")
public class DmController {
    private final DmService dmService;

    @GetMapping
    public CommonResponse<List<DmUserResponse>> getDmUserList() {
        Long currentId = SecurityUtil.getCurrentUserId();
        List<DmUserResponse> dmUsers = dmService.findDmUsers(currentId);
        return new CommonResponse<>(true, HttpStatus.OK,"모든 DM이 반환되었습니다.",dmUsers);
    }

    @PostMapping
    public CommonResponse<DmAddResponse> startDm(@RequestBody DmStartRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        DmAddResponse dmAddResponse = dmService.findOrCreateDm(currentUserId, Long.valueOf(request.getTargetId()));

        return new CommonResponse<>(true, HttpStatus.OK, "DM 생성 성공", dmAddResponse);
    }

    @PostMapping
    @RequestMapping("/visible")
    public CommonResponse<DmVisibleResponse> notVisibleDm(@RequestBody DmVisibleRequest request) {
        DmVisibleResponse dmVisibleResponse = dmService.nonVisibleDm(request);
        return new CommonResponse<>(true, HttpStatus.OK, "DM 숨기기 완료", dmVisibleResponse);
    }

}
