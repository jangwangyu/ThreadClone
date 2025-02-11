package com.example.board.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "post")
@SQLDelete(sql = "UPDATE \"post\" SET deleteddatetime = CURRENT_TIMESTAMP WHERE postid = ? ") // delete를 update로 바꿔서 soft delete를 하는 방법임
@SQLRestriction("deleteddatetime IS NULL")
public class PostEntity {
  @Id // primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY) // key 생성 전략은 identity 게시물 생성마다 1증가
  private Long id;

  @Column(columnDefinition = "TEXT") // 데이터 상 TEXT로 생성
  private String body; // 게시물 본문

  @Column
  private ZonedDateTime createdDateTime; // 만든 시간

  @Column
  private ZonedDateTime updateDateTime; // 수정 시간

  @Column
  private ZonedDateTime deleteDateTime; // 삭제 시간


  public PostEntity(Long id, String body, ZonedDateTime createdDateTime,
      ZonedDateTime updateDateTime, ZonedDateTime deleteDateTime) {
    this.id = id;
    this.body = body;
    this.createdDateTime = createdDateTime;
    this.updateDateTime = updateDateTime;
    this.deleteDateTime = deleteDateTime;
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

  public ZonedDateTime getCreatedDateTime() {
    return createdDateTime;
  }

  public void setCreatedDateTime(ZonedDateTime createdDateTime) {
    this.createdDateTime = createdDateTime;
  }

  public ZonedDateTime getUpdateDateTime() {
    return updateDateTime;
  }

  public void setUpdateDateTime(ZonedDateTime updateDateTime) {
    this.updateDateTime = updateDateTime;
  }

  public ZonedDateTime getDeleteDateTime() {
    return deleteDateTime;
  }

  public void setDeleteDateTime(ZonedDateTime deleteDateTime) {
    this.deleteDateTime = deleteDateTime;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    PostEntity that = (PostEntity) o;
    return Objects.equals(id, that.id) && Objects.equals(body, that.body) && Objects.equals(
        createdDateTime, that.createdDateTime) && Objects.equals(updateDateTime,
        that.updateDateTime) && Objects.equals(deleteDateTime, that.deleteDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, body, createdDateTime, updateDateTime, deleteDateTime);
  }
}
