package com.example.board.service;

import com.example.board.exception.post.PostNotFoundException;
import com.example.board.model.Post;
import com.example.board.model.PostPatchRequestBody;
import com.example.board.model.PostPostRequestBody;
import com.example.board.model.entity.PostEntity;
import com.example.board.repository.PostEntityRepository;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PostService {

  @Autowired
  private PostEntityRepository postEntityRepository;

//  public List<Post> getPosts() {
//    return posts;
//  } JPA 적용 전

  public List<Post> getPosts() {
    var postEntities = postEntityRepository.findAll();
    return postEntities.stream().map(Post::from).toList();
  }


  public Post getPostByPostId(Long postId) {
    var postEntity = postEntityRepository.findById(postId)
        .orElseThrow(() -> new PostNotFoundException(postId));

    return Post.from(postEntity);
  }


  public Post createPost(PostPostRequestBody postPostRequestBody) {
    var postEntity = new PostEntity();
    postEntity.setBody(postPostRequestBody.body());
    var savedpostEntidy = postEntityRepository.save(postEntity);
    return Post.from(savedpostEntidy);
  }


  public Post updatePost(Long postId,PostPatchRequestBody postPatchRequestBody) {
    var postEntity = postEntityRepository
        .findById(postId)
        .orElseThrow(
            () -> new PostNotFoundException(postId));
    postEntity.setBody(postPatchRequestBody.body());

    var updatedPostEntity = postEntityRepository.save(postEntity);
    return Post.from(updatedPostEntity);
  }

  public void deletePost(Long postId) {
    var postEntity = postEntityRepository
        .findById(postId)
        .orElseThrow(
            () -> new PostNotFoundException(postId));
    postEntityRepository.delete(postEntity);
  }
}
