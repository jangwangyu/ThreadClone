package com.example.board.model.user;

import com.example.board.model.entity.UserEntity;
import java.time.ZonedDateTime;

public record User(
    Long userId,
    String username,
    String profile,
    String description,
    Long followersCount,
    Long followingsCount,
    ZonedDateTime createdDateTime,
    ZonedDateTime updatedDateTime,
    Boolean isFollowing
) {
  public static User from(UserEntity userEntity) {
    return new User(
        userEntity.getUserId(),
        userEntity.getUsername(),
        userEntity.getProfile(),
        userEntity.getDescription(),
        userEntity.getFollowersCount(),
        userEntity.getFollowingsCount(),
        userEntity.getCreateddatetime(),
        userEntity.getUpdateddatetime(),
        null);
  }

  public static User from(UserEntity userEntity, boolean isfollowing) {
    return new User(
        userEntity.getUserId(),
        userEntity.getUsername(),
        userEntity.getProfile(),
        userEntity.getDescription(),
        userEntity.getFollowersCount(),
        userEntity.getFollowingsCount(),
        userEntity.getCreateddatetime(),
        userEntity.getUpdateddatetime(),
        isfollowing);
  }

}
