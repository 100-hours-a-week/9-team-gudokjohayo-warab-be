package store.warab.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import store.warab.jwt.JWTFilter;
import store.warab.jwt.JWTUtil;
import store.warab.oauth2.CustomSuccessHandler;
import store.warab.service.CustomOAuth2UserService;

// import store.warab.service.CustomOAuth2UserService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.security.config.Customizer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final CustomOAuth2UserService customOAuth2UserService;
  private final CustomSuccessHandler customSuccessHandler;
  private final JWTUtil jwtUtil;

  @Value("${cors.allowed-origin}")
  private String corsAllowedOrigin;

  public SecurityConfig(
      CustomOAuth2UserService customOAuth2UserService,
      CustomSuccessHandler customSuccessHandler,
      JWTUtil jwtUtil) {
    this.customOAuth2UserService = customOAuth2UserService;
    this.customSuccessHandler = customSuccessHandler;
    this.jwtUtil = jwtUtil;
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
                    (userInfoEndpointConfig) ->
                        userInfoEndpointConfig.userService(customOAuth2UserService))
                .successHandler(customSuccessHandler)
                .failureHandler(
                    (request, response, exception) -> {
                      System.out.println("OAuth 로그인 실패: " + exception.getMessage());
                      response.sendRedirect("/login?error"); // 실패시 리다이렉트
                    }));

    // JWT Filter 추가
    http.addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

    // 경로별 인가 작업
    http.authorizeHttpRequests(
        (auth) ->
            auth.requestMatchers("/", "/api/health", "/api/dev/login", "/api/v1/auth/logout")
                .permitAll()
                .anyRequest()
                .authenticated());

    // 경로별 인가 작업
    //              http.authorizeHttpRequests(
    //                  (auth) -> auth.
    //                      anyRequest().permitAll()); // 모든 요청 허용

    // 세션 설정
    http.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    return http.build();
  }
}
