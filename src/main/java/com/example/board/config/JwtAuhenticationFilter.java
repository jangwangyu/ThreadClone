package com.example.board.config;

import com.example.board.exception.jwt.JwtTokenNotFoundException;
import com.example.board.service.JwtService;
import com.example.board.service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuhenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtService jwtService;

  @Autowired
  private UserService userService;


  @Override
  protected void doFilterInternal
      (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // JWT 검증
    // httpRequestHeader에서 AUTHORIZATION 값 추출해서 BEARER_PREFIX 빼서 실제 jwt 토큰 값을 받아오고
    String BEARER_PREFIX = "Bearer ";
    var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
    var securityContext = SecurityContextHolder.getContext();

    // authorization.startsWith(BEARER_PREFIX)이 없을때 예외
    if(ObjectUtils.isEmpty(authorization) || !authorization.startsWith(BEARER_PREFIX)) {
      throw new JwtTokenNotFoundException();
    }

    // 인증을 받아온 적이 없고 해당 토큰이 정상적인 문자열일 때
    if(!ObjectUtils.isEmpty(authorization)
      && authorization.startsWith(BEARER_PREFIX)
      && securityContext.getAuthentication() == null) {

      // jwt 서비스를 통해서 해당 토큰을 검증하고, username을 추출하고 username을 바탕으로 실제 사용자 정보도 조회
      var accessToken = authorization.substring(BEARER_PREFIX.length()); // BEARER_PREFIX.length() 길이만큼 잘라내서 액세스 토큰값 추출
      var username = jwtService.getUsername(accessToken); // jwtService.getUsername(accessToken) username에 token을 var username에 주입
      var userDetails = userService.loadUserByUsername(username); // userservice에 있는 loadUserByUsername 메소드를 userDetails에 삽임

      // 조회한 사용자 정보를 이후에 httpRequest를 처리하는 로직을 controller에서 그대로 사용할 수 있게끔 securityContext에 설정해준다
      var authenticationToken = new UsernamePasswordAuthenticationToken(
          userDetails, null, userDetails.getAuthorities());
      authenticationToken.setDetails(
          new WebAuthenticationDetailsSource().buildDetails(request));
      securityContext.setAuthentication(authenticationToken);
      SecurityContextHolder.setContext(securityContext);
    }
      filterChain.doFilter(request, response); // doFilter 호출해서 이후 필터들이 정상적으로 실행되게끔
  }

}
