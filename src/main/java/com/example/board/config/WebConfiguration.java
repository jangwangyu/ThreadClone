package com.example.board.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebConfiguration {

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
    http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated()) // 모든 request에서 인증처리를 한다
        .sessionManagement(
            (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // session은 생성되지 않는다
        .csrf(CsrfConfigurer::disable) // csrf 검증은 제외
        .httpBasic(Customizer.withDefaults()); // basicAuth 를 사용

    return http.build();
  }
}
