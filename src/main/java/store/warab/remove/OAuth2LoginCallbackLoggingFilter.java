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
public class OAuth2LoginCallbackLoggingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getRequestURI().contains("/login/oauth2/code/kakao")) {
      String sessionId =
          request.getSession(false) != null ? request.getSession(false).getId() : "no session";
      Sentry.captureMessage("🔁 redirect URI 도착 - session ID: " + sessionId);

      Object saved =
          request.getSession(false) != null
              ? request.getSession(false).getAttribute("SPRING_SECURITY_SAVED_REQUEST")
              : null;
      Sentry.captureMessage(
          "🔁 Saved Auth Request: " + (saved != null ? saved.toString() : "null"));
    }
    filterChain.doFilter(request, response);
    // test
  }
}
