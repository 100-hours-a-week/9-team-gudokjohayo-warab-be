package store.warab.oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import store.warab.entity.User;
import store.warab.jwt.JWTUtil;
import store.warab.repository.UserRepository;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  private final JWTUtil jwtUtil;
  private final UserRepository userRepository;

  @Value("${redirect.oauth2.after.login}")
  private String oauthRedirect;

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
            .orElseThrow(() -> new RuntimeException("User with id " + kakaoId + " not found"));

    Long userId = user.getId();

    // JWT 생성
    String token = jwtUtil.createJwt(userId, 60 * 60 * 200 * 60L);

    // 쿠키에 저장 후 리다이렉션
    response.addCookie(createCookie("jwt", token));
    response.sendRedirect(oauthRedirect);
  }

  private Cookie createCookie(String key, String value) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(60 * 60 * 60);
    // cookie.setSecure(true);
    cookie.setPath("/");
    cookie.setHttpOnly(true);

    return cookie;
  }
}
