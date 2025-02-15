package com.example.board.service;

import com.example.board.exception.follow.FollowAlreadyExistsException;
import com.example.board.exception.follow.FollowNotFoundException;
import com.example.board.exception.follow.InvalidFollowException;
import com.example.board.exception.user.UserAlreadyExistsException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.FollowEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserPatchRequestBody;
import com.example.board.repository.FollowEntityRepository;
import com.example.board.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
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
  private FollowEntityRepository followEntityRepository;

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

  public List<User> getUsers(String query, UserEntity currentUser) {
    List<UserEntity> userEntities;

    if(query != null && !query.isBlank()) {
      // query 검색어 기반, 해당 검색어가 username에 포함되어 있는 유저목록 가져오기
      // JPA에서 기본으로 주는 기능이 아니기 때문에 별도로 만들어줘야함
      userEntities = userEntityRepository.findByUsernameContaining(query); // 이름이 포함되어 있는 모든 유저 검색
    } else {
      userEntities = userEntityRepository.findAll(); // 모든 유저 검색
    }

    return userEntities.stream().map(
        userEntity -> getUserWithFollowingStatus(userEntity, currentUser)).toList();
  }

  public User getUser(String username, UserEntity currentUser) {
    var userEntity =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    return getUserWithFollowingStatus(userEntity, currentUser);
  }

  private User getUserWithFollowingStatus(UserEntity userEntity, UserEntity currentUser) {
    var isFollowing =
        followEntityRepository.findByFollowerAndFollowing(currentUser, userEntity).isPresent();

    return User.from(userEntity, isFollowing);
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

  @Transactional
  public User follow(String username, UserEntity currentUser) {
    var following =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    if(following.equals(currentUser)) {
      throw new InvalidFollowException("A user connot follow themselves.");
    }

    followEntityRepository.findByFollowerAndFollowing(currentUser, following)
        .ifPresent(
            follow -> {
              throw new FollowAlreadyExistsException(currentUser, following);
            }
        );

    followEntityRepository.save(FollowEntity.of(currentUser, following));

    following.setFollowersCount(following.getFollowersCount() + 1);
    currentUser.setFollowersCount(following.getFollowingsCount() + 1);

//    userEntityRepository.save(following);
//    userEntityRepository.save(currentUser);
    userEntityRepository.saveAll(List.of(following, currentUser)); // List로 담아서 saveall로 한번에 처리 가능

    return User.from(following, true);
  }

  @Transactional
  public User unfollow(String username, UserEntity currentUser) {
    var following =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    if(following.equals(currentUser)) {
      throw new InvalidFollowException("A user connot unfollow themselves.");
    }

    var followEntity =
        followEntityRepository.findByFollowerAndFollowing(currentUser, following)
        .orElseThrow(
            () -> new FollowNotFoundException(currentUser, following)
        );

    followEntityRepository.delete(followEntity);

    following.setFollowersCount(Math.max(0,following.getFollowersCount() - 1));
    currentUser.setFollowersCount(Math.max(0,following.getFollowingsCount() - 1));

    userEntityRepository.saveAll(List.of(following, currentUser)); // List로 담아서 saveall로 한번에 처리 가능

    return User.from(following, false);
  }

  public List<User> getFollowersByUsername(String username, UserEntity currentUser) {
    var following =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    var followEntities = followEntityRepository.findByFollowing(following);

    return followEntities.stream().map(
        follow -> getUserWithFollowingStatus(follow.getFollower(), currentUser)).toList(); // User.from(follow.getFollower())로 변환시킨
  }

  public List<User> getFollowingsByUsername(String username, UserEntity currentUser) {
    var followers =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    var followEntities = followEntityRepository.findByFollower(followers);

    return followEntities.stream().map(
        follow -> getUserWithFollowingStatus(follow.getFollowing(), currentUser)).toList();
  }
}
