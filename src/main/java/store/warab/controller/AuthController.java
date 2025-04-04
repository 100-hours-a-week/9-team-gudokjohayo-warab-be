package store.warab.controller;

import io.sentry.Sentry;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.util.ApiResponse;
import store.warab.dto.AuthUserResponseDto;
import store.warab.jwt.JWTUtil;
import store.warab.service.AuthService;
import store.warab.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;
  private final UserService userService;
  private final JWTUtil jwtUtil;

  public AuthController(AuthService authService, UserService userService, JWTUtil jwtUtil) {
    this.authService = authService;
    this.userService = userService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) throws IOException {
    // JWT 토큰이 저장된 쿠키를 제거
    Cookie cookie = new Cookie("jwt", null);
    cookie.setMaxAge(0); // 쿠키 즉시 만료
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);

    response.addCookie(cookie);
    // response.sendRedirect("/login");

    return ResponseEntity.ok().body("로그아웃에 성공하였습니다.");
  }

  @GetMapping("/me")
  public ResponseEntity<ApiResponse> getCurrentUser(
      @CookieValue(value = "jwt", required = false) String token) {
    // 토큰이 없는 경우 (로그인하지 않은 사용자)
    if (token == null) {
      return ResponseEntity.ok(new ApiResponse("not_authenticated", null));
    }

    try {
      // 토큰 만료 확인
      if (jwtUtil.isTokenExpired(token)) {
        return ResponseEntity.ok(new ApiResponse("token_expired", null));
      }

      // 토큰에서 사용자 ID 추출
      Long userId = authService.extractUserId(token);

      // 사용자 정보 조회
      AuthUserResponseDto userInfo = userService.getAuthUserInfo(userId);

      return ResponseEntity.ok(new ApiResponse("authenticated", userInfo));
    } catch (Exception e) {
      Sentry.captureException(e);
      return ResponseEntity.ok(new ApiResponse("invalid_token", null));
    }
  }
}
