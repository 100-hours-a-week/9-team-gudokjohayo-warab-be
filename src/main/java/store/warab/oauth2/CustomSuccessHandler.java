package store.warab.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.warab.common.exception.NotFoundException;
import store.warab.entity.User;
import store.warab.jwt.JWTUtil;
import store.warab.repository.UserRepository;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;

  @Value("${redirect.oauth2.after.login}")
  private String oauthRedirect;

  @Value("${spring.profiles.active}")
  private String activeProfile;

  public CustomSuccessHandler(JWTUtil jwtUtil, UserRepository userRepository) {

    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException, ServletException {

    // OAuth2User
    OAuth2User customUserDetails = (OAuth2User) authentication.getPrincipal();
    // String username = customUserDetails.getAttribute("username");
    Long kakaoIdNum = customUserDetails.getAttribute("id");
    String kakaoId = String.valueOf(kakaoIdNum);

    // DB에서 사용자 정보 조회
    User user =
        userRepository
            .findByKakaoId(kakaoId)
            .orElseThrow(() -> new NotFoundException("User with id " + kakaoId + " not found"));

    Long userId = user.getId();

    // JWT 생성
    String token = jwtUtil.createJwt(userId, 60 * 60 * 200 * 60L);

    String setDomain = isProd() ? "warab.store" : "dev.warab.store";

    ResponseCookie cookie =
        ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None") // ✅ 크로스사이트 대응
            .domain(setDomain) // ✅ 프론트 도메인과 공유되게 설정
            .path("/")
            .maxAge(Duration.ofDays(1))
            .build();

    response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    //    response.flushBuffer();

    logger.info("✅ 프론트로 리다이렉트: " + oauthRedirect);

    // 쿠키에 저장 후 리다이렉션
    //    response.addCookie(createCookie("jwt", token));
    response.sendRedirect(oauthRedirect);
  }

  public boolean isProd() {
    return "prod".equalsIgnoreCase(activeProfile);
  }

  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(60 * 60 * 60 * 200);
    cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setHttpOnly(true);

    if (isProd()) {
      cookie.setDomain(".warab.store");
    }

    return cookie;
  }
}
