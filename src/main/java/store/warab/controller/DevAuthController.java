package store.warab.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.warab.entity.User;
import store.warab.jwt.JWTUtil;
import store.warab.repository.UserRepository;

@RestController
@RequestMapping("/api/dev")
@Profile("dev") // 운영에선 자동 비활성화됨
public class DevAuthController {
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public DevAuthController(JWTUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> devLogin(
        @RequestParam Long userId,
        HttpServletResponse response) {

        String jwt = jwtUtil.createJwt(45L, 60 * 60 * 24L); // 1일짜리 JWT
        Cookie cookie = new Cookie("jwt", jwt);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24); // 1일
        response.addCookie(cookie);

        return ResponseEntity.ok("개발용 JWT 쿠키 발급 완료");
    }
}
