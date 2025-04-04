package store.warab.remove;

import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class OAuth2LoginStartLoggingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String uri = request.getRequestURI();
    if (uri.contains("/oauth2/authorization")) {
      Sentry.captureMessage("🟢 /oauth2/authorization 요청 들어옴 - OAuth2 흐름 시작");
    }
    filterChain.doFilter(request, response);
  }
}
