package dev.discord_server.domain.notification.controller;

import dev.discord_server.auth.util.SecurityUtil;
import dev.discord_server.common.response.CommonResponse;
import dev.discord_server.domain.notification.dto.NotificationListResponse;
import dev.discord_server.domain.server.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<NotificationListResponse> getNotifications() {
        Long uuid = SecurityUtil.getCurrentUserId();
        return new CommonResponse<>(true, HttpStatus.OK, "알림 목록입니다.", notificationService.getNotifications(uuid));
    }

    @PatchMapping("/read")
    @PreAuthorize("hasRole('USER')")
    public CommonResponse<Void> markAllAsRead() {
        Long uuid = SecurityUtil.getCurrentUserId();
        notificationService.markAllAsRead(uuid);
        return new CommonResponse<>(true, HttpStatus.OK, "알림을 모두 읽음 처리했습니다.", null);
    }
}
