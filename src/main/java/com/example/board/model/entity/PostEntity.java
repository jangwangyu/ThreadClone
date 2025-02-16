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
@Table(name = "post", indexes = {@Index(name = "post_userid_idx", columnList = "userid")})
// userid에 대한 index
@SQLDelete(sql = "UPDATE \"post\" SET deleteddatetime = CURRENT_TIMESTAMP WHERE id = ? ")
// delete를 update로 바꿔서 soft delete를 하는 방법임
@SQLRestriction("deleteddatetime IS NULL")
public class PostEntity {

  @Id // primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY) // key 생성 전략은 identity 게시물 생성마다 1증가
  private Long id;

  @Column(columnDefinition = "TEXT") // 데이터 상 TEXT로 생성
  private String body; // 게시물 본문

  @Column
  private Long repliesCount = 0L;

  @Column
  private Long likesCount = 0L;

  @Column
  private ZonedDateTime createdDateTime; // 만든 시간

  @Column
  private ZonedDateTime updatedDateTime; // 수정 시간

  @Column
  private ZonedDateTime deletedDateTime; // 삭제 시간

  @ManyToOne
  @JoinColumn(name = "userid")
  private UserEntity user;


  public PostEntity(Long id, String body, ZonedDateTime createdDateTime,
      ZonedDateTime updateDateTime, ZonedDateTime deleteDateTime) {
    this.id = id;
    this.body = body;
    this.createdDateTime = createdDateTime;
    this.updatedDateTime = updateDateTime;
    this.deletedDateTime = deleteDateTime;
  }

  public PostEntity() {

  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Long getRepliesCount() {
    return repliesCount;
  }

  public void setRepliesCount(Long repliesCount) {
    this.repliesCount = repliesCount;
  }

  public Long getLikesCount() {
    return likesCount;
  }

  public void setLikesCount(Long likesCount) {
    this.likesCount = likesCount;
  }

  public ZonedDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(ZonedDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public ZonedDateTime getUpdatedDateTime() {
    return updatedDateTime;
  }

  public void setUpdatedDateTime(ZonedDateTime updatedDateTime) {
    this.updatedDateTime = updatedDateTime;
  }

  public ZonedDateTime getDeletedDateTime() {
    return deletedDateTime;
  }

  public void setDeletedDateTime(ZonedDateTime deletedDateTime) {
    this.deletedDateTime = deletedDateTime;
  }

  public UserEntity getUser() {
    return user;
  }

  public void setUser(UserEntity user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PostEntity that = (PostEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(body, that.body)
        && Objects.equals(repliesCount, that.repliesCount) && Objects.equals(
        likesCount, that.likesCount) && Objects.equals(createdDateTime, that.createdDateTime)
        && Objects.equals(updatedDateTime, that.updatedDateTime)
        && Objects.equals(deletedDateTime, that.deletedDateTime)
        && Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, body, repliesCount, likesCount, createdDateTime, updatedDateTime,
        deletedDateTime, user);
  }

  public static PostEntity of(String body, UserEntity user) {
    var post = new PostEntity();
    post.setBody(body);
    post.setUser(user);
    return post;
  }

  // JPA에 의해서 실제 데이터와 내부적으로 저장되기 직전, 혹은 수정되기 직전에 원하는 로직을 수행할 수 있음
  @PrePersist
  private void prePersist() {
    this.createdDateTime = ZonedDateTime.now();
    this.updatedDateTime = ZonedDateTime.now();
  }

  @PreUpdate
  private void preUpdate() {
    this.updatedDateTime = ZonedDateTime.now();
  }

}
