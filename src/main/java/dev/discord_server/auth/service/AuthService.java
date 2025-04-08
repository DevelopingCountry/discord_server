package dev.discord_server.auth.service;

import dev.discord_server.auth.converter.AuthConverter;
import dev.discord_server.auth.dto.KakaoDTO;
import dev.discord_server.auth.repository.RefreshTokenRepository;
import dev.discord_server.auth.util.JwtUtil;
import dev.discord_server.auth.util.KakaoUtil;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.entity.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final KakaoUtil kakaoUtil;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    public User oAuthLogin(String accessCode, HttpServletResponse httpServletResponse) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String email = kakaoProfile.getKakao_account().getEmail();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(kakaoProfile));


        // Access Token & Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getEmail(), user.getRole());
        log.info("Access token: {}", accessToken);
        log.info("Refresh token: {}", refreshToken);


        // Refresh Token을 Redis에 저장
        refreshTokenRepository.saveRefreshToken(user.getEmail(), refreshToken);

        // Access Token을 응답 헤더에 추가
        httpServletResponse.setHeader("Authorization", accessToken);
        httpServletResponse.setHeader("Refresh-Token", refreshToken);

        return user;

    }

    public String refreshAccessToken(String refreshToken) {

        String email;
        try {
            email = jwtUtil.getEmailFromToken(refreshToken);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT");
        }

        System.out.println(email);

        String storedToken = refreshTokenRepository.getRefreshToken(email);

        System.out.println(storedToken);

        // ✅ 여기 로그 추가!
        System.out.println("📩 요청된 refreshToken: " + refreshToken);
        System.out.println("📦 Redis에 저장된 refreshToken: " + storedToken);
        System.out.println("⛔ equals 비교 결과: " + refreshToken.equals(storedToken));

        if (storedToken != null && storedToken.equals(refreshToken)) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
            return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token Expired or Not Found");
    }



    private User createNewUser(KakaoDTO.KakaoProfile kakaoProfile) {
        User newUser = AuthConverter.toUser(
                kakaoProfile.getKakao_account().getEmail(),
                kakaoProfile.getKakao_account().getProfile().getNickname(),
                Role.USER

        );
        return userRepository.save(newUser);
    }

}
