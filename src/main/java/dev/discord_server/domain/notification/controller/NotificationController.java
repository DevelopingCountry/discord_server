package dev.discord_server.domain.notification.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.notification.dto.NotificationListResponse;
import dev.discord_server.domain.server.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "알림 API", description = "서버 초대 등 알림 목록 조회 및 읽음 처리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "로그인한 유저의 알림 목록과 안 읽은 알림 개수를 반환합니다.")
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<NotificationListResponse> getNotifications() {
        Long uuid = SecurityUtil.getCurrentUserId();
        return new CommonResponse<>(true, HttpStatus.OK, "알림 목록입니다.", notificationService.getNotifications(uuid));
    }

    @Operation(summary = "알림 전체 읽음 처리", description = "로그인한 유저의 모든 알림을 읽음 상태로 변경합니다.")
    @PatchMapping("/read")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> markAllAsRead() {
        Long uuid = SecurityUtil.getCurrentUserId();
        notificationService.markAllAsRead(uuid);
        return new CommonResponse<>(true, HttpStatus.OK, "알림을 모두 읽음 처리했습니다.", null);
    }
}
