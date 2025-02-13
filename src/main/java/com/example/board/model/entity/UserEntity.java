package com.example.board.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "\"users\"")
@SQLDelete(sql = "UPDATE \"user\" SET deleteddatetime = CURRENT_TIMESTAMP WHERE userid = ? ") // delete를 update로 바꿔서 soft delete를 하는 방법임
@SQLRestriction("deleteddatetime IS NULL")
public class UserEntity implements UserDetails {

  @Id // primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY) // key 생성 전략은 identity 게시물 생성마다 1증가
  private Long userId;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column
  private String profile;

  @Column
  private String description;

  @Column
  private ZonedDateTime createddatetime;

  @Column
  private ZonedDateTime updateddatetime;

  @Column
  private ZonedDateTime deleteddatetime;

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getProfile() {
    return profile;
  }

  public void setProfile(String profile) {
    this.profile = profile;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public ZonedDateTime getCreateddatetime() {
    return createddatetime;
  }

  public void setCreateddatetime(ZonedDateTime createddatetime) {
    this.createddatetime = createddatetime;
  }

  public ZonedDateTime getUpdateddatetime() {
    return updateddatetime;
  }

  public void setUpdateddatetime(ZonedDateTime updateddatetime) {
    this.updateddatetime = updateddatetime;
  }

  public ZonedDateTime getDeleteddatetime() {
    return deleteddatetime;
  }

  public void setDeleteddatetime(ZonedDateTime deleteddatetime) {
    this.deleteddatetime = deleteddatetime;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass())
      return false;
    UserEntity that = (UserEntity) o;
    return Objects.equals(userId, that.userId) && Objects.equals(username, that.username)
        && Objects.equals(password, that.password) && Objects.equals(profile, that.profile)
        && Objects.equals(description, that.description) && Objects.equals(createddatetime,
        that.createddatetime) && Objects.equals(updateddatetime, that.updateddatetime)
        && Objects.equals(deleteddatetime, that.deleteddatetime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, username, password, profile, description, createddatetime,
        updateddatetime, deleteddatetime);
  }

  public static UserEntity of(String username, String password) {
    var userEntity = new UserEntity();
    userEntity.setUsername(username);
    userEntity.setPassword(password);

    // Avatar Placeholder 서비스 (https://avatar-placeholder.iran.liara.run) 기반
    // 랜덤한 프로필 사진 설정(1 ~ 100)
    userEntity
        .setProfile("https://avatar.iran.liara.run/public/" + (new Random().nextInt(100) + 1));

    // 위 API가 정상적으로 동작하지 않을 경우, 이것을 사용
//    userEntity
//        .setProfile("https://dev-jayce.github.io/public/profile/" + (new Random().nextInt(100) + 1) + ".png");

    return userEntity;
  }

  // JPA에 의해서 실제 데이터와 내부적으로 저장되기 직전, 혹은 수정되기 직전에 원하는 로직을 수행할 수 있음
  @PrePersist
  private void prePersist() {
    this.createddatetime = ZonedDateTime.now();
    this.updateddatetime = ZonedDateTime.now();
  }

  @PreUpdate
  private void preUpdate() {
    this.updateddatetime = ZonedDateTime.now();
  }
}
