package com.example.board.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(
    name = "\"follow\"",
    indexes = {
        @Index(name = "follow_follower_following_idx", columnList = "follower, following", unique = true)})
public class FollowEntity {

  @Id // primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY) // key 생성 전략은 identity 게시물 생성마다 1증가
  private Long followId;

  @Column
  private ZonedDateTime createdDateTime; // n분 전 좋아요

  @ManyToOne
  @JoinColumn(name = "follower")
  private UserEntity follower;

  @ManyToOne
  @JoinColumn(name = "following")
  private UserEntity following;

  public Long getFollowId() {
    return followId;
  }

  public void setLikeId(Long likeId) {
    this.followId = followId;
  }

  public ZonedDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(ZonedDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public void setFollowId(Long followId) {
    this.followId = followId;
  }

  public UserEntity getFollower() {
    return follower;
  }

  public void setFollower(UserEntity follower) {
    this.follower = follower;
  }

  public UserEntity getFollowing() {
    return following;
  }

  public void setFollowing(UserEntity following) {
    this.following = following;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FollowEntity that = (FollowEntity) o;
    return Objects.equals(followId, that.followId) && Objects.equals(
        createdDateTime, that.createdDateTime) && Objects.equals(follower, that.follower)
        && Objects.equals(following, that.following);
  }

  @Override
  public int hashCode() {
    return Objects.hash(followId, createdDateTime, follower, following);
  }

  public static FollowEntity of(UserEntity follower, UserEntity following) {
    FollowEntity follow = new FollowEntity();
    follow.setFollower(follower);
    follow.setFollowing(following);
    return follow;
  }

  // JPA에 의해서 실제 데이터와 내부적으로 저장되기 직전, 혹은 수정되기 직전에 원하는 로직을 수행할 수 있음
  @PrePersist
  private void prePersist() {
    this.createdDateTime = ZonedDateTime.now();
  }

}
