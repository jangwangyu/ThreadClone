package com.example.board.service;

import com.example.board.exception.user.UserAlreadyExistsException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserPatchRequestBody;
import com.example.board.repository.UserEntityRepository;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
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

  public List<User> getUsers(String query) {
    List<UserEntity> userEntities;

    if(query != null && !query.isBlank()) {
      // query 검색어 기반, 해당 검색어가 username에 포함되어 있는 유저목록 가져오기
      // JPA에서 기본으로 주는 기능이 아니기 때문에 별도로 만들어줘야함
      userEntities = userEntityRepository.findByUsernameContaining(query); // 이름이 포함되어 있는 모든 유저 검색
    } else {
      userEntities = userEntityRepository.findAll(); // 모든 유저 검색
    }

    return userEntities.stream().map(User::from).toList();
  }

  public User getUser(String username) {
    var userEntity =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    return User.from(userEntity);
  }

  public User updateUser(String username, UserPatchRequestBody userPatchRequestBody, UserEntity currentUser) {

    var userEntity =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    if(!userEntity.equals(currentUser)) {
      throw new UserNotAllowedException();
    }

    if(userPatchRequestBody.description() != null) { // userPatchRequestBody으로 전달받은 description이 null이 아닐때 description을 수정
      userEntity.setDescription(userPatchRequestBody.description());
    }

    return User.from(userEntityRepository.save(userEntity));
  }
}
