package com.example.board.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class WebConfiguration {

  @Autowired
  private JwtAuhenticationFilter jwtAuhenticationFilter;
  @Autowired
  private JwtExceptionFilter jwtExceptionFilter;



  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration(); // Cors 설정 툴
    configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000")); // 허용하고자 하는 Origin 목록 설정
    configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE")); // api가 호출될때 어떤 http 메소드를 허용할지 설정
    configuration.setAllowedHeaders(List.of("*")); // 모든 값 허용 , (List.of("Authorization")) 처럼 허용하고 싶은거만 추가해도 됨
    // configuration.setAllowCredentials(true); // 자격 증명 허용 (쿠키, 인증 헤더 등)
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/v1/**", configuration); // 특정 api cors 설정
    return source;
  }

  // SpringBootWebSecurityConfiguration 커스터마이징
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http
        .cors(Customizer.withDefaults()) // cors 설정 기본값
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers(HttpMethod.POST, "/api/*/users")
            .permitAll()
            .anyRequest()
            .authenticated()) // 모든 request에서 인증처리를 한다
        .sessionManagement(
            (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session은 생성되지 않는다
        .csrf(CsrfConfigurer::disable) // csrf 검증은 제외
        .addFilterBefore(jwtAuhenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtExceptionFilter, jwtAuhenticationFilter.getClass())
        .httpBasic(Customizer.withDefaults()); // basicAuth 를 사용

    return http.build();
  }
}
