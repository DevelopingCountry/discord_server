package dev.discord_server.auth.service;

import dev.discord_server.auth.converter.AuthConverter;
import dev.discord_server.auth.dto.KakaoDTO;
import dev.discord_server.auth.dto.TokenResponse;
import dev.discord_server.auth.repository.RefreshTokenRepository;
import dev.discord_server.auth.util.JwtUtil;
import dev.discord_server.auth.util.KakaoUtil;
import dev.discord_server.config.SnowflakeIdGenerator;
import dev.discord_server.domain.nickname.service.NicknameService;
import dev.discord_server.domain.user.Enum.Role;
import dev.discord_server.domain.user.entity.User;
import dev.discord_server.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final SnowflakeIdGenerator snowflakeIdGenerator;
    private final NicknameService nicknameService;

    @Transactional
    public TokenResponse oAuthLogin(String accessCode) {
        KakaoDTO.OAuthToken oAuthToken = kakaoUtil.requestToken(accessCode);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestProfile(oAuthToken);
        String email = resolveEmail(kakaoProfile);

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email));


        // Access Token & Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getEmail(), user.getRole());
        String userId = String.valueOf(user.getId());
        log.info("Access token: {}", accessToken);
        log.info("Refresh token: {}", refreshToken);
        log.info("userId: {}", userId);


        // Refresh Token을 Redis에 저장
        refreshTokenRepository.saveRefreshToken(user.getEmail(), refreshToken);

        // Access Token을 응답 헤더에 추가
        return new TokenResponse(accessToken, refreshToken,userId);

    }

    // 로컬 개발용 - 카카오 OAuth 없이 이메일만으로 로그인/가입 (SecurityConfig에서 !prod 프로필에서만 노출)
    public TokenResponse devLogin(String email, String nickname) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(AuthConverter.toUser(
                        snowflakeIdGenerator.generateId(),
                        email,
                        nickname != null && !nickname.isBlank() ? nickname : nicknameService.assignRandomNickname(),
                        Role.USER
                )));

        String accessToken = jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getId(), user.getEmail(), user.getRole());
        String userId = String.valueOf(user.getId());

        refreshTokenRepository.saveRefreshToken(user.getEmail(), refreshToken);

        return new TokenResponse(accessToken, refreshToken, userId);
    }

    public String refreshAccessToken(String refreshToken) {

        String email;
        try {
            email = jwtUtil.getEmailFromToken(refreshToken);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT");
        }

        String storedToken = refreshTokenRepository.getRefreshToken(email);

        if (storedToken != null && storedToken.equals(refreshToken)) {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
            return jwtUtil.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh Token Expired or Not Found");
    }



    private User createNewUser(String email) {
        User newUser = AuthConverter.toUser(
                snowflakeIdGenerator.generateId(),
                email,
                nicknameService.assignRandomNickname(),
                Role.USER

        );
        return userRepository.save(newUser);
    }

    private String resolveEmail(KakaoDTO.KakaoProfile kakaoProfile) {
        String email = kakaoProfile.getKakao_account().getEmail();
        return email != null ? email : "k" + kakaoProfile.getId();
    }

}