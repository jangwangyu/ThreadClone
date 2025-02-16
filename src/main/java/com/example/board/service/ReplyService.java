package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.reply.ReplyNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.ReplyEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.model.reply.Reply;
import com.example.board.model.reply.ReplyPatchRequestBody;
import com.example.board.model.reply.ReplyPostRequestBody;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.ReplyEntityRepository;
import com.example.board.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyService {

  @Autowired
  private ReplyEntityRepository replyEntityRepository;

  @Autowired
  private PostEntityRepository postEntityRepository;

  @Autowired
  private UserEntityRepository userEntityRepository;
  private static final Logger logger = LoggerFactory.getLogger(ReplyService.class);

  public List<Reply> getRepliesByPostId(long postId) { // 댓글 목록 API
    var postEntity =
        postEntityRepository
            .findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    var replyEntities = replyEntityRepository.findByPost(postEntity);

    return replyEntities.stream()
        .map(Reply::from) // Reply::from이 ReplyEntity를 입력으로 받아야 합니다.
        .toList();
  }

  @Transactional
  public Reply createReply(Long postId, ReplyPostRequestBody replyPostRequestBody,
      UserEntity currentUser) { // 댓글 달기
    var postEntity = postEntityRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId));

    var replyEntity =
        replyEntityRepository.save(
            ReplyEntity.of(replyPostRequestBody.body(), currentUser, postEntity));

    postEntity.setRepliesCount(postEntity.getRepliesCount() + 1); // postEntity에 있는 repliesCount를 가져와서 1을 증가시켜줌.

    return Reply.from(replyEntity);
  }



  public Reply updateReply(Long postId, Long replyId, ReplyPatchRequestBody replyPatchRequestBody,UserEntity currentUser) {
    postEntityRepository
        .findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId));

    var replyEntity =
        replyEntityRepository.findById(replyId)
            .orElseThrow(() -> new ReplyNotFoundException(replyId));

    if (!replyEntity.getUser().equals(currentUser)) {
      throw new UserNotAllowedException();
    }

    logger.info("updateReply", Reply.from(replyEntityRepository.save(replyEntity)));
    replyEntity.setBody(replyPatchRequestBody.body());
    return Reply.from(replyEntityRepository.save(replyEntity));
  }

  @Transactional
  public void deleteReply(Long postId, Long replyId, UserEntity currentUser) {
    var postEntity = postEntityRepository
        .findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId));

    var replyEntity =
        replyEntityRepository.findById(replyId)
            .orElseThrow(() -> new ReplyNotFoundException(replyId));

    if (!replyEntity.getUser().equals(currentUser)) {
      throw new UserNotAllowedException();
    }

    replyEntityRepository.delete(replyEntity);

    postEntity.setRepliesCount(Math.max(0, postEntity.getRepliesCount() - 1));
    postEntityRepository.save(postEntity);
  }


  public List<Reply> getRepliesByUser(String username) {
    var userEntity =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    var replyEntities = replyEntityRepository.findByUser(userEntity); // 유저가 작성한 모든 댓굴
    return replyEntities.stream().map(Reply::from).toList();
  }
}
