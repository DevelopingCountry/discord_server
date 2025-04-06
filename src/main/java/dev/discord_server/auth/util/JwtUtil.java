package dev.discord_server.auth.util;

import dev.discord_server.domain.user.Enum.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.accessSecret}")
    private String SECRET_KEY;
    @Value("${jwt.refreshSecret}")
    private String REFRESH_SECRET;
    @Value("${jwt.access_token_expired_at}")
    private long ACCESS_TOKEN_EXPIRED_AT;
    @Value("${jwt.refresh_token_expired_at}")
    private long REFRESH_TOKEN_EXPIRED_AT;



    public String createAccessToken(Long userId, String email, Role role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRED_AT))
                .signWith(accessGetSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId, String email, Role role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRED_AT))
                .signWith(refreshGetSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessGetSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(accessGetSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }




    private Key accessGetSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private Key refreshGetSigningKey() {
        byte[] keyBytes = REFRESH_SECRET.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public Long getUserIdFromToken(String token) {
        return Long.parseLong(getAllClaims(token).getSubject());
    }


    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessGetSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}

