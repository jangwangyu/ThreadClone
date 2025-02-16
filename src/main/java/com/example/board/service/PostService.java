package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.LikeEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.post.PostPatchRequestBody;
import com.example.board.model.post.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.LikeEntityRepository;
import com.example.board.repository.PostEntityRepository;
import com.example.board.repository.UserEntityRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostService {

  @Autowired
  private PostEntityRepository postEntityRepository;

  @Autowired
  private UserEntityRepository userEntityRepository;

  @Autowired
  private LikeEntityRepository likeEntityRepository;

//  public List<Post> getPosts() {
//    return posts;
//  } JPA 적용 전

  public List<Post> getPosts(UserEntity currentUser) {
    var postEntities = postEntityRepository.findAll();

    return postEntities.stream()
        .map(postEntity -> getPostWithLikingStatus(postEntity, currentUser))
        .toList();
  }


  public Post getPostByPostId(Long postId, UserEntity currentUser) {
    var postEntity = postEntityRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId));

    return getPostWithLikingStatus(postEntity, currentUser);

  }

  private Post getPostWithLikingStatus(PostEntity postEntity, UserEntity currentUser) {
    var isLiking = likeEntityRepository.findByUserAndPost(currentUser, postEntity).isPresent();
    return Post.from(postEntity, isLiking);
  }


  public Post createPost(PostPostRequestBody postPostRequestBody, UserEntity currentUser) {

    var postEntity = postEntityRepository.save(
        PostEntity.of(postPostRequestBody.body(), currentUser)
    );
    return Post.from(postEntity);
  }


  public Post updatePost(Long postId, PostPatchRequestBody postPatchRequestBody,
      UserEntity currentUser) {
    var postEntity = postEntityRepository
        .findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId));

    if (!postEntity.getUser().equals(currentUser)) {
      throw new UserNotAllowedException();
    }

    postEntity.setBody(postPatchRequestBody.body());

    var updatedPostEntity = postEntityRepository.save(postEntity);
    return Post.from(updatedPostEntity);
  }

  public void deletePost(Long postId, UserEntity currentUser) {
    var postEntity = postEntityRepository
        .findById(postId)
        .orElseThrow(
            () -> new PostNotFoundException(postId));

    if (!postEntity.getUser().equals(currentUser)) {
      throw new UserNotAllowedException();
    }

    postEntityRepository.delete(postEntity);
  }

  public List<Post> getPostsByUsername(String username, UserEntity currentUser) {
    var userEntity =
        userEntityRepository
            .findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(username));

    var postEntities = postEntityRepository.findByUser(userEntity);
    return postEntities.stream()
        .map(postEntity -> getPostWithLikingStatus(postEntity, currentUser))
        .toList();
  }

  @Transactional
  public Post toggleLike(Long postId, UserEntity currentUser) {
    var postEntity = postEntityRepository
        .findById(postId)
        .orElseThrow(
            () -> new PostNotFoundException(postId));

    var likeEntity = likeEntityRepository.findByUserAndPost(currentUser, postEntity);

    if (likeEntity.isPresent()) {
      likeEntityRepository.delete(likeEntity.get());
      postEntity.setLikesCount(Math.max(0, postEntity.getLikesCount() - 1));
      return Post.from(postEntityRepository.save(postEntity), false);
    } else {
      likeEntityRepository.save(LikeEntity.of(currentUser, postEntity));
      postEntity.setLikesCount(postEntity.getLikesCount() + 1);
      return Post.from(postEntityRepository.save(postEntity),true);
    }

  }
}
