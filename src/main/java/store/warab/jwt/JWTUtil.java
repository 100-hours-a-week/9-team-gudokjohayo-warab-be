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

  public Boolean isExpired(String token) {

    return Jwts.parser()
        .verifyWith(secretKey)
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getExpiration()
        .before(new Date());
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
      throw new RuntimeException("유효하지 않은 JWT 토큰입니다.", e);
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
