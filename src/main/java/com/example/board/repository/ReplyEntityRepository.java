package com.example.board.repository;

import com.example.board.model.entity.PostEntity;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyEntityRepository extends JpaRepository<ReplyEntity, Long> {

  List<PostEntity> findByUser(UserEntity user);

  List<ReplyEntity> findByPost(PostEntity post);

}
