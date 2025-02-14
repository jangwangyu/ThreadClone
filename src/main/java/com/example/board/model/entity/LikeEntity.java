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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Objects;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(
    name = "\"like\"",
    indexes = {
        @Index(name = "like_userid_postid_idx", columnList = "userid, postid", unique = true)})
public class LikeEntity {

  @Id // primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY) // key 생성 전략은 identity 게시물 생성마다 1증가
  private Long likeId;

  @Column
  private ZonedDateTime createdDateTime; // n분 전 좋아요

  @ManyToOne
  @JoinColumn(name = "userid")
  private UserEntity user;

  @ManyToOne
  @JoinColumn(name = "postid")
  private PostEntity post;

  public Long getLikeId() {
    return likeId;
  }

  public void setLikeId(Long likeId) {
    this.likeId = likeId;
  }

  public ZonedDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(ZonedDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  public PostEntity getPost() {
    return post;
  }

  public void setPost(PostEntity post) {
    this.post = post;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LikeEntity that = (LikeEntity) o;
    return Objects.equals(likeId, that.likeId) && Objects.equals(createdDateTime,
        that.createdDateTime) && Objects.equals(user, that.user)
        && Objects.equals(post, that.post);
  }

  @Override
  public int hashCode() {
    return Objects.hash(likeId, createdDateTime, user, post);
  }

  public static LikeEntity of(UserEntity user, PostEntity post){
    var like = new LikeEntity();
    like.setUser(user);
    like.setPost(post);
    return like;
  }

  // JPA에 의해서 실제 데이터와 내부적으로 저장되기 직전, 혹은 수정되기 직전에 원하는 로직을 수행할 수 있음
  @PrePersist
  private void prePersist() {
    this.createdDateTime = ZonedDateTime.now();
  }

}
