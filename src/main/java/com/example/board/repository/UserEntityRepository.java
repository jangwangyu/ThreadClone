package com.example.board.repository;

import com.example.board.model.entity.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserEntityRepository extends JpaRepository<UserEntity, Long> {
  Optional<UserEntity> findByUsername(String username); // 유니크한 타입으로 만들 예정

  List<UserEntity> findByUsernameContaining(String username);
}
