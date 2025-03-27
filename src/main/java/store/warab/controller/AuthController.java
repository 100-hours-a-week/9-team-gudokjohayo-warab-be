package store.warab.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response) throws IOException {
    // JWT 토큰이 저장된 쿠키를 제거
    Cookie cookie = new Cookie("Authorization", null);
    cookie.setMaxAge(0); // 쿠키 즉시 만료
    cookie.setPath("/");
    cookie.setHttpOnly(true);
    cookie.setSecure(true);

    response.addCookie(cookie);
    //response.sendRedirect("/login");

    return ResponseEntity.ok().body("로그아웃에 성공하였습니다.");
  }
}
