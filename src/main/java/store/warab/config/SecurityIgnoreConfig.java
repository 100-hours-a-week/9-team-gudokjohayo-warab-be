package store.warab.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityIgnoreConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // ✅ CSRF 보호 비활성화 (개발 중 불필요)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // ✅ 모든 요청을 인증 없이 허용
            )
            .formLogin(form -> form.disable()) // ✅ 기본 로그인 페이지 비활성화
            .httpBasic(basic -> basic.disable()); // ✅ HTTP Basic 인증 비활성화

        return http.build();
    }
}
