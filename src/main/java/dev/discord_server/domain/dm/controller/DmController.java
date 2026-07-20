package dev.discord_server.domain.dm.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.dm.dto.DmAddResponse;
import dev.discord_server.domain.dm.dto.DmUserResponse;
import dev.discord_server.domain.dm.dto.DmVisibleRequest;
import dev.discord_server.domain.dm.dto.DmVisibleResponse;
import dev.discord_server.domain.dm.service.DmService;
import dev.discord_server.domain.dm_message.dto.DmStartRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "DM 대화 API", description = "DM 대화 목록 조회/생성, 숨김 처리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/dm")
public class DmController {
    private final DmService dmService;

    @Operation(summary = "DM 대화 목록 조회", description = "로그인한 유저의 DM 대화 목록을 반환합니다.")
    @GetMapping
    public CommonResponse<List<DmUserResponse>> getDmUserList() {
        Long currentId = SecurityUtil.getCurrentUserId();
        List<DmUserResponse> dmUsers = dmService.findDmUsers(currentId);
        return new CommonResponse<>(true, HttpStatus.OK,"모든 DM이 반환되었습니다.",dmUsers);
    }

    @Operation(summary = "DM 시작 또는 조회", description = "상대 유저와의 DM 대화가 있으면 반환하고, 없으면 새로 생성합니다.")
    @PostMapping
    public CommonResponse<DmAddResponse> startDm(@RequestBody DmStartRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        DmAddResponse dmAddResponse = dmService.findOrCreateDm(currentUserId, Long.valueOf(request.getTargetId()));

        return new CommonResponse<>(true, HttpStatus.OK, "DM 생성 성공", dmAddResponse);
    }

    @Operation(summary = "DM 숨김/표시 토글", description = "DM 대화 목록에서 해당 DM을 숨기거나 다시 표시합니다.")
    @PostMapping("/visible")
    public CommonResponse<DmVisibleResponse> notVisibleDm(@RequestBody DmVisibleRequest request) {
        DmVisibleResponse dmVisibleResponse = dmService.nonVisibleDm(request);
        return new CommonResponse<>(true, HttpStatus.OK, "DM 숨기기 완료", dmVisibleResponse);
    }

}
