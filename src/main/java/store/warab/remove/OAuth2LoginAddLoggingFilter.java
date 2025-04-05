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
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OAuth2LoginAddLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 모든 요청에 대해 세션 정보 로깅 (로그인 전/후 상태 모두 캡처하기 위함)
    logSessionState(request, "필터 시작 지점");

    // 카카오 OAuth 콜백 URL인 경우 상세 로깅
    if (request.getRequestURI().contains("/login/oauth2/code/kakao")) {
      logKakaoOAuthCallback(request);
    }

    try {
      filterChain.doFilter(request, response);
    } finally {
      // 필터 체인 완료 후 세션 상태 다시 로깅
      logSessionState(request, "필터 종료 지점");
    }
  }

  private void logSessionState(HttpServletRequest request, String phase) {
    try {
      HttpSession session = request.getSession(false);
      String sessionId = session != null ? session.getId() : "null";
      String requestURI = request.getRequestURI();

      StringBuilder sb = new StringBuilder();
      sb.append("🔍 [")
          .append(phase)
          .append("] URI: ")
          .append(requestURI)
          .append(", Session ID: ")
          .append(sessionId);

      if (session != null) {
        // 주요 Spring Security 속성 확인
        Object authentication = session.getAttribute("SPRING_SECURITY_CONTEXT");
        sb.append(", Auth: ").append(authentication != null ? "present" : "null");
      }

      Sentry.captureMessage(sb.toString());
    } catch (Exception e) {
      Sentry.captureException(e);
    }
  }

  private void logKakaoOAuthCallback(HttpServletRequest request) {
    try {
      StringBuilder logBuilder = new StringBuilder();
      logBuilder.append("🔁 카카오 OAuth 콜백 정보 (상세):\n");

      // 기본 요청 정보
      logBuilder.append("URI: ").append(request.getRequestURI()).append("\n");
      logBuilder.append("Method: ").append(request.getMethod()).append("\n");
      logBuilder.append("RemoteAddr: ").append(request.getRemoteAddr()).append("\n");
      logBuilder.append("UserAgent: ").append(request.getHeader("User-Agent")).append("\n");
      logBuilder.append("Referer: ").append(request.getHeader("Referer")).append("\n");

      // 세션 정보 로깅
      HttpSession session = request.getSession(false);
      if (session != null) {
        logBuilder.append("Session ID: ").append(session.getId()).append("\n");
        logBuilder.append("Session Creation Time: ").append(session.getCreationTime()).append("\n");
        logBuilder
            .append("Session Last Accessed: ")
            .append(session.getLastAccessedTime())
            .append("\n");
        logBuilder
            .append("Session Max Inactive Interval: ")
            .append(session.getMaxInactiveInterval())
            .append("\n");

        // Spring Security 관련 세션 속성 체크
        Object savedRequest = session.getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        logBuilder
            .append("Saved Auth Request: ")
            .append(savedRequest != null ? "present" : "null")
            .append("\n");

        Object securityContext = session.getAttribute("SPRING_SECURITY_CONTEXT");
        logBuilder
            .append("Security Context: ")
            .append(securityContext != null ? "present" : "null")
            .append("\n");

        Object oauth2AuthorizationRequest =
            session.getAttribute(
                "org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository.AUTHORIZATION_REQUEST");
        logBuilder
            .append("OAuth2 Authorization Request: ")
            .append(oauth2AuthorizationRequest != null ? "present" : "null")
            .append("\n");

        // 세션의 모든 속성 키 로깅
        logBuilder.append("Session Attributes: \n");
        Enumeration<String> attributeNames = session.getAttributeNames();
        boolean hasAttributes = false;
        while (attributeNames.hasMoreElements()) {
          hasAttributes = true;
          String name = attributeNames.nextElement();
          Object value = session.getAttribute(name);
          logBuilder
              .append("  - ")
              .append(name)
              .append("=")
              .append(value != null ? value.getClass().getSimpleName() : "null")
              .append("\n");
        }
        if (!hasAttributes) {
          logBuilder.append("  none\n");
        }
      } else {
        logBuilder.append("⚠️ Session: NULL (로그인 실패 가능성 높음)\n");
      }

      // 쿠키 정보 로깅
      logBuilder.append("Cookies: \n");
      Cookie[] cookies = request.getCookies();
      if (cookies != null && cookies.length > 0) {
        for (Cookie cookie : cookies) {
          logBuilder
              .append("  - ")
              .append(cookie.getName())
              .append("=")
              .append(cookie.getValue() != null ? cookie.getValue().length() + "chars" : "null")
              .append(", Domain: ")
              .append(cookie.getDomain())
              .append(", Path: ")
              .append(cookie.getPath())
              .append(", MaxAge: ")
              .append(cookie.getMaxAge())
              .append(", Secure: ")
              .append(cookie.getSecure())
              .append(", HttpOnly: ")
              .append(cookie.isHttpOnly())
              .append("\n");
        }
      } else {
        logBuilder.append("  none\n");
      }

      // 요청 파라미터 로깅
      logBuilder.append("Parameters: \n");
      Map<String, String[]> parameterMap = request.getParameterMap();
      if (!parameterMap.isEmpty()) {
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
          String key = entry.getKey();
          String[] values = entry.getValue();

          // 민감한 정보는 마스킹 처리
          if ("code".equals(key)) {
            logBuilder
                .append("  - ")
                .append(key)
                .append("=present(")
                .append(values.length > 0 && values[0] != null ? values[0].length() : 0)
                .append("chars)\n");
          } else if ("error".equals(key)) {
            // 에러 파라미터가 있으면 중요하므로 값 그대로 로깅
            logBuilder
                .append("  - ")
                .append(key)
                .append("=")
                .append(values.length > 0 ? values[0] : "empty")
                .append("\n");
          } else {
            logBuilder
                .append("  - ")
                .append(key)
                .append("=")
                .append(values.length > 0 ? values[0] : "empty")
                .append("\n");
          }
        }
      } else {
        logBuilder.append("  none\n");
      }

      // 모든 헤더 정보 로깅
      logBuilder.append("Headers: \n");
      Enumeration<String> headerNames = request.getHeaderNames();
      if (headerNames != null) {
        while (headerNames.hasMoreElements()) {
          String headerName = headerNames.nextElement();
          // 민감한 헤더 정보는 제외
          if (!"cookie".equalsIgnoreCase(headerName)
              && !"authorization".equalsIgnoreCase(headerName)) {
            logBuilder
                .append("  - ")
                .append(headerName)
                .append(": ")
                .append(request.getHeader(headerName))
                .append("\n");
          }
        }
      }

      // 로그 메시지 전송
      String logMessage = logBuilder.toString();
      Sentry.captureMessage(logMessage);

      // 세션 ID만 별도로 간단히 로깅
      String sessionId = session != null ? session.getId() : "null";
      if (session == null) {
        Sentry.captureMessage("⚠️ 카카오 콜백 도착 - 세션이 NULL입니다! 인증 실패 가능성 높음");
      } else {
        Sentry.captureMessage("✅ 카카오 콜백 도착 - session ID: " + sessionId);
      }

    } catch (Exception e) {
      // 로깅 과정에서 오류가 발생해도 원래 요청 처리에는 영향을 주지 않도록 함
      Sentry.captureException(e);
    }
  }
}
