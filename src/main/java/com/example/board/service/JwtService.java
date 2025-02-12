package com.example.board.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import java.util.Date;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final SecretKey key = SIG.HS256.key().build();
  private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

  public String generateAccessToken(UserDetails userDetails) {
    return generateToken(userDetails.getUsername()); // username을 추출해 jwt 토큰으로 만들고 generateAccessToken으로 만듬
  }

  public String getUsername(String accesstoken) {
    return getSubject(accesstoken); //accesstoken으로부터 저장되어 있는 subject를 추출해(username) 돌려주는 getUsername 메소드
  }


  private String generateToken(String subject) { // 토큰 생성
    var now = new Date(); // 시작 시점
    var exp = new Date(now.getTime() + (1000 * 60 * 60 * 3)); // 만료 시점 3시간 이후

    return Jwts.builder().subject(subject).signWith(key)
        .issuedAt(now)
        .expiration(exp)
        .compact();
  }

  private String getSubject(String token) { // jwt를 생성할때 넣은 subject를 추출하는 함수
    try{
      return Jwts.parser()
          .verifyWith(key)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getSubject();
    }catch (JwtException e){
      logger.error("JwtException", e);
      throw e;
    }
  }
}
