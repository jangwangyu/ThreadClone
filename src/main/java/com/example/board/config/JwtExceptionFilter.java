package com.example.board.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain)
      throws ServletException, IOException {

      try{
        filterChain.doFilter(request, response);
      }catch (JwtException exception){
        // JWT 관련 커스텀 에러메시지 생성해 RESPONSE로 내려주기
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401 응답
      response.setCharacterEncoding("utf-8");

      var errorMap = new HashMap<String, Object>();
      errorMap.put("status", HttpStatus.UNAUTHORIZED);
      errorMap.put("message", exception.getMessage());

      ObjectMapper objectMapper = new ObjectMapper();
      String responseJson = objectMapper.writeValueAsString(errorMap); // 직렬화
      response.getWriter().write(responseJson); // 응답으로 내려줌
    }
  }
}
