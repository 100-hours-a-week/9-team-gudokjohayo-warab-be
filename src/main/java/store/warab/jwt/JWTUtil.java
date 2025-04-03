package store.warab.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import store.warab.common.exception.InvalidTokenException;

@Component
public class JWTUtil {

  private SecretKey secretKey;

  public JWTUtil(@Value("${spring.jwt.secret}") String secret) {
    secretKey =
        new SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
  }

  public String getUsername(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .get("username", String.class);
  }

  // JWT 토큰 생성
  public String createJwt(Long userId, Long expiredMs) {

    return Jwts.builder()
        .claim("userId", userId)
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + expiredMs))
        .signWith(secretKey)
        .compact();
  }

  // JWT 토큰에서 ID 추출
  public Long getUserIdFromToken(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

      return claims.get("userId", Long.class);

    } catch (JwtException e) {
      throw new InvalidTokenException("유효하지 않은 JWT 토큰입니다.");
    }
  }

  // 토큰 유효성 검사
  public boolean validateToken(String token) {
    try {
      // 토큰 파싱해서 유효한지 확인 (서명 검증 포함)
      Jwts.parser()
          .verifyWith(secretKey) // HMAC 또는 RSA 키
          .build()
          .parseSignedClaims(token);

      return true;
    } catch (JwtException | IllegalArgumentException e) {
      // 유효하지 않은 토큰
      return false;
    }
  }

  // 토큰 만료 확인
  public boolean isTokenExpired(String token) {
    try {
      Claims claims =
          Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

      return claims.getExpiration().before(new Date());
    } catch (JwtException e) {
      return true; // 토큰 파싱 중 에러가 발생하면 만료된 것으로 처리
    }
  }
}
