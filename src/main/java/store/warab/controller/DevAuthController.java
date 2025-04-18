package store.warab.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.warab.jwt.JWTUtil;

@RestController
@RequestMapping("/api/dev")
public class DevAuthController {
  private final JWTUtil jwtUtil;

  public DevAuthController(JWTUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/login")
  public ResponseEntity<String> devLogin(HttpServletResponse response) {

    String jwt = jwtUtil.createJwt(45L, 1000 * 60 * 60 * 24L * 500); // 500일
    Cookie cookie = new Cookie("jwt", jwt);
    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setMaxAge(60 * 60 * 24); // 1일
    response.addCookie(cookie);

    return ResponseEntity.ok("개발용 JWT 쿠키 발급 완료");
  }
}
