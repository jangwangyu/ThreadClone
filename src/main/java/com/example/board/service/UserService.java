package com.example.board.service;

import com.example.board.exception.user.UserAlreadyExistsException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.repository.UserEntityRepository;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserEntityRepository userEntityRepository;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Autowired
  private JwtService jwtService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userEntityRepository
        .findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException(username));
  }

  public User signUp(String username, String password) {
    userEntityRepository.findByUsername(username)
        .ifPresent(
            user -> {
              throw new UserAlreadyExistsException();
            });

    var userEntity = userEntityRepository
        .save(UserEntity.of(username, passwordEncoder.encode(password)));

    return User.from(userEntity);
  }

  public UserAuthenticationResponse authenticate(@NotEmpty String username, @NotEmpty String password) {
    var userEntity =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    // 인코딩된 패스워드와 userEntity에 있는 패스워드 비교
    if(passwordEncoder.matches(password, userEntity.getPassword())) {
      var accessToken = jwtService.generateAccessToken(userEntity);
      return new UserAuthenticationResponse(accessToken);
    }else {
      throw new UserNotFoundException();
    }
  }
}
