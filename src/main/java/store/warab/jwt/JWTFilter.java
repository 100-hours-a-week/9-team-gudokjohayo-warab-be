package store.warab.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import store.warab.common.security.CustomUserDetails;
import store.warab.entity.User;

public class JWTFilter extends OncePerRequestFilter {
  private final JWTUtil jwtUtil;

  public JWTFilter(JWTUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String requestUri = request.getRequestURI();
    if (requestUri.matches("^/login(?:/.*)?$")) {
      filterChain.doFilter(request, response);
      return;
    }
    if (requestUri.matches("^/oauth2(?:/.*)?$")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = null;
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("jwt".equals(cookie.getName())) { // JWT가 담긴 쿠키 이름
          token = cookie.getValue();
          break;
        }
      }
    }

    if (token != null && !jwtUtil.isTokenExpired(token)) {
      Long userId = jwtUtil.getUserIdFromToken(token);

      // User 객체를 생성하여 CustomUserDetails에 전달
      User user = new User();
      user.setId(userId); // userId 설정

      CustomUserDetails customUserDetails = new CustomUserDetails(user, Collections.emptyMap());

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(
              customUserDetails, null, customUserDetails.getAuthorities());

      SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    filterChain.doFilter(request, response);
  }
}
