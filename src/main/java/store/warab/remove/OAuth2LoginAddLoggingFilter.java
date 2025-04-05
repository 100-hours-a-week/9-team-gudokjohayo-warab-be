package store.warab.remove;

import io.sentry.Sentry;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class OAuth2LoginAddLoggingFilter extends OncePerRequestFilter {
  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getRequestURI().contains("/login/oauth2/code/kakao")) {
      logKakaoOAuthCallback(request);
    }
    filterChain.doFilter(request, response);
  }

  private void logKakaoOAuthCallback(HttpServletRequest request) {
    try {
      StringBuilder logBuilder = new StringBuilder();
      logBuilder.append("🔁 카카오 OAuth 콜백 정보:\n");

      // 기본 요청 정보
      logBuilder.append("URI: ").append(request.getRequestURI()).append("\n");
      logBuilder.append("Method: ").append(request.getMethod()).append("\n");
      logBuilder.append("RemoteAddr: ").append(request.getRemoteAddr()).append("\n");
      logBuilder.append("UserAgent: ").append(request.getHeader("User-Agent")).append("\n");

      // 세션 정보 로깅
      HttpSession session = request.getSession(false);
      if (session != null) {
        logBuilder.append("Session ID: ").append(session.getId()).append("\n");
        logBuilder.append("Session Creation Time: ").append(session.getCreationTime()).append("\n");
        logBuilder
            .append("Session Last Accessed: ")
            .append(session.getLastAccessedTime())
            .append("\n");

        Object savedRequest = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        logBuilder
            .append("Saved Auth Request: ")
            .append(savedRequest != null ? "present" : "null")
            .append("\n");

        // 세션의 모든 속성 키 로깅
        logBuilder.append("Session Attributes: ");
        Enumeration<String> attributeNames = session.getAttributeNames();
        boolean hasAttributes = false;
        while (attributeNames.hasMoreElements()) {
          if (hasAttributes) {
            logBuilder.append(", ");
          }
          String name = attributeNames.nextElement();
          Object value = session.getAttribute(name);
          logBuilder
              .append(name)
              .append("=")
              .append(value != null ? value.getClass().getSimpleName() : "null");
          hasAttributes = true;
        }
        if (!hasAttributes) {
          logBuilder.append("none");
        }
        logBuilder.append("\n");
      } else {
        logBuilder.append("Session: none\n");
      }

      // 쿠키 정보 로깅
      logBuilder.append("Cookies: ");
      Cookie[] cookies = request.getCookies();
      if (cookies != null && cookies.length > 0) {
        for (int i = 0; i < cookies.length; i++) {
          if (i > 0) {
            logBuilder.append(", ");
          }
          Cookie cookie = cookies[i];
          logBuilder
              .append(cookie.getName())
              .append("=")
              .append(cookie.getValue() != null ? cookie.getValue().length() + "chars" : "null");
        }
      } else {
        logBuilder.append("none");
      }
      logBuilder.append("\n");

      // 요청 파라미터 로깅
      logBuilder.append("Parameters: ");
      Map<String, String[]> parameterMap = request.getParameterMap();
      if (!parameterMap.isEmpty()) {
        boolean first = true;
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
          if (!first) {
            logBuilder.append(", ");
          }
          first = false;

          String key = entry.getKey();
          String[] values = entry.getValue();

          // 민감한 정보는 마스킹 처리
          if ("code".equals(key)) {
            logBuilder
                .append(key)
                .append("=present(")
                .append(values.length > 0 && values[0] != null ? values[0].length() : 0)
                .append("chars)");
          } else {
            logBuilder.append(key).append("=").append(values.length > 0 ? values[0] : "empty");
          }
        }
      } else {
        logBuilder.append("none");
      }

      // 로그 메시지 전송
      String logMessage = logBuilder.toString();
      Sentry.captureMessage(logMessage);

      // 세션 ID만 별도로 간단히 로깅
      String sessionId = session != null ? session.getId() : "null";
      Sentry.captureMessage("🔁 redirect URI 도착 - session ID: " + sessionId);

    } catch (Exception e) {
      // 로깅 과정에서 오류가 발생해도 원래 요청 처리에는 영향을 주지 않도록 함
      Sentry.captureException(e);
    }
  }
}
