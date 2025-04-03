package store.warab.service;

import org.springframework.stereotype.Service;
import store.warab.common.exception.ForbiddenException;
import store.warab.common.exception.InvalidTokenException;
import store.warab.jwt.JWTUtil;

@Service
public class AuthService {

  private final JWTUtil jwtUtil;

  public AuthService(JWTUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  /**
   * 토큰에서 사용자 ID 추출
   *
   * @param token JWT 토큰
   * @return 추출된 사용자 ID
   */
  public Long extractUserId(String token) {
    if (token == null) {
      throw new InvalidTokenException("유효하지 않은 토큰입니다.");
    }
    return jwtUtil.getUserIdFromToken(token);
  }

  public boolean isValid(String token) {
    try {
      if (token == null) return false;
      return jwtUtil.validateToken(token); // 이 내부에서 JwtException 던질 수 있음
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * 사용자 ID가 동일한지 확인
   *
   * @param tokenUserId 토큰에서 추출한 사용자 ID
   * @param userId 요청에서 전달된 사용자 ID
   */
  public void verifyUser(Long tokenUserId, Long userId) {
    if (!tokenUserId.equals(userId)) {
      throw new ForbiddenException("권한이 없습니다.");
    }
  }
}
