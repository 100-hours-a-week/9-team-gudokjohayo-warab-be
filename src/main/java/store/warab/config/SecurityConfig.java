package store.warab.config;

import io.sentry.Sentry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import store.warab.jwt.JWTFilter;
import store.warab.jwt.JWTUtil;
import store.warab.oauth2.CustomSuccessHandler;
import store.warab.remove.OAuth2LoginCallbackLoggingFilter;
import store.warab.remove.OAuth2LoginStartLoggingFilter;
import store.warab.service.CustomOAuth2UserService;

// import store.warab.service.CustomOAuth2UserService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.config.Customizer;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomSuccessHandler customSuccessHandler;
  private final JWTUtil jwtUtil;

  @Value("${cors.allowed-origin}")
  private String corsAllowedOrigin;

  @Value("${redirect.oauth2.after.login}")
  private String redirectOauth2AfterLogin;

  public SecurityConfig(
      CustomOAuth2UserService customOAuth2UserService,
      CustomSuccessHandler customSuccessHandler,
      JWTUtil jwtUtil) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.customSuccessHandler = customSuccessHandler;
    this.jwtUtil = jwtUtil;
    Sentry.captureMessage("✅ customOAuth2UserService 주입됨1: " + customOAuth2UserService);
    Sentry.captureMessage(
        "✅ customOAuth2UserService 주입됨2: " + customOAuth2UserService.getOAuth2UserForDebug());
  }

  @Bean
  public AuthenticationEntryPoint customAuthenticationEntryPoint() {
    return (request, response, authException) -> {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null && auth.isAuthenticated()) {
        // DispatcherServlet으로 넘겨서 GlobalExceptionHandler에서 처리되게 함
        request.setAttribute("exception", authException);
        request.getRequestDispatcher(request.getRequestURI()).forward(request, response);
        return;
      }

      // 인증이 안 된 경우는 여기서 직접 401 응답
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"error\": \"인증되지 않은 요청입니다.\"}");
    };
  }

  @Bean
  public AccessDeniedHandler customAccessDeniedHandler() {
    return (request, response, accessDeniedException) -> {
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      response.setContentType("application/json;charset=UTF-8");
      response.getWriter().write("{\"error\": \"접근 권한이 없습니다.\"}");
    };
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.exceptionHandling(
        exception ->
            exception
                .authenticationEntryPoint(customAuthenticationEntryPoint())
                .accessDeniedHandler(customAccessDeniedHandler()));

    // CORS
    http.cors(
        corsCustomizer ->
            corsCustomizer.configurationSource(
                new CorsConfigurationSource() {

                  @Override
                  public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                    CorsConfiguration configuration = new CorsConfiguration();

                    configuration.setAllowedOrigins(Collections.singletonList(corsAllowedOrigin));
                    // origin URL 추가할 때 쉼표로 구분
                    //                    configuration.setAllowedOrigins(
                    //                        Arrays.asList(corsAllowedOrigin.split(",")));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setMaxAge(3600L);
                    configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

                    // configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                    // configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                    return configuration;
                  }
                }));

    // csrf disable
    http.csrf((auth) -> auth.disable());

    // From 로그인 방식 disable
    http.formLogin((auth) -> auth.disable());

    // HTTP Basic 인증 방식 disable
    http.httpBasic((auth) -> auth.disable());

    // oauth2
    http.oauth2Login(
        (oauth2) ->
            oauth2
                .userInfoEndpoint(
                    (userInfoEndpointConfig) -> {
                      Sentry.captureMessage("🟡 userInfoEndpoint 설정 진입 - loadUser() 이전 단계");
                      userInfoEndpointConfig.userService(customOAuth2UserService);
                    })
                .successHandler(customSuccessHandler)
                .failureHandler(
                    (request, response, exception) -> {
                      Sentry.withScope(
                          scope -> {
                            Sentry.captureMessage("🔴 enter in failureHandler");
                            scope.setExtra(
                                "customOAuth2UserService",
                                String.valueOf(customOAuth2UserService)); // 여기다 변수들 추가하면 됨!
                            scope.setExtra("redirectURL", redirectOauth2AfterLogin);
                            scope.setExtra("exceptionClass", exception.getClass().getName());
                            scope.setExtra("exceptionMessage", exception.getMessage());
                            scope.setExtra("redirectURL", redirectOauth2AfterLogin);
                            Sentry.captureException(exception); // 그냥 원래 예외 던지는 게 디버깅엔 더 도움됨
                          });
                      log.error("OAuth 로그인 실패: {}", exception.getMessage(), exception);
                      response.sendRedirect("/login?error"); // 실패시 리다이렉트
                    }));

    // JWT Filter 추가
    // SecurityConfig 에 등록
    http.addFilterBefore(
            new OAuth2LoginStartLoggingFilter(), OAuth2AuthorizationRequestRedirectFilter.class)
        .addFilterBefore(
            new OAuth2LoginCallbackLoggingFilter(), OAuth2LoginAuthenticationFilter.class);

    http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

    // 경로별 인가 작업
    http.authorizeHttpRequests(
        (auth) ->
            auth.requestMatchers("/", "/api/health", "/api/v1/**", "/api/dev/login")
                .permitAll()
                .anyRequest()
                .authenticated());

    // 경로별 인가 작업
    //              http.authorizeHttpRequests(
    //                  (auth) -> auth.
    //                      anyRequest().permitAll()); // 모든 요청 허용

    // 세션 설정
    // http.sessionManagement(
    //     session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

    return http.build();
  }
}
