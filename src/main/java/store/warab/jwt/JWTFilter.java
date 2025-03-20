package store.warab.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import store.warab.common.security.CustomUserDetails;
import store.warab.entity.User;

import java.io.IOException;
import java.util.Collections;

public class JWTFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt".equals(cookie.getName())) { // JWT가 담긴 쿠키 이름을 "jwt"로 가정
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null && !jwtUtil.isExpired(token)) {
            String username = jwtUtil.getUsername(token);

            // User 객체를 생성하여 CustomUserDetails에 전달
            User user = new User();
            user.setNickname(username); // 필요한 정보 설정

            CustomUserDetails customUserDetails = new CustomUserDetails(user, Collections.emptyMap());

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                customUserDetails, null, customUserDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response);
    }
}
