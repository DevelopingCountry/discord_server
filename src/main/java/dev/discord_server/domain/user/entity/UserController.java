package dev.discord_server.domain.user.entity;

import dev.discord_server.auth.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getUserInfo() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        System.out.println(currentUserId);

        return ResponseEntity.ok("You are logged in!");
    }
}


//TODO: role 추가,현재 로그인한 유저 받아오는 로직 추가 완료
//TODO: DB 변경, entity 변경

