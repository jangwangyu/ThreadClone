package com.example.board.controller;

import com.example.board.model.Post;
import com.example.board.model.PostPatchRequestBody;
import com.example.board.model.PostPostRequestBody;
import com.example.board.service.PostService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts") // 공통된 url은 RequestMapping 어노테이션을 써서 명시적으로 구분 후 GetMapping에서 삭제 가능
public class PostController {

  @Autowired private PostService postService;

  @GetMapping
  public ResponseEntity<List<Post>> getPosts() {

    var posts = postService.getPosts();

    return ResponseEntity.ok(posts);
  }

  @GetMapping("/{postId}")
  public ResponseEntity<Post> getPostByPostId(@PathVariable Long postId) {
    Optional<Post> matchingPost = postService.getPostByPostId(postId);

    return matchingPost
        .map(ResponseEntity::ok) // 요청한 Response가 존재할때
        .orElseGet(() -> ResponseEntity.notFound().build()); // 요청한 Response가 존재하지 않을 때
  }

  // POST -> /posts
  @PostMapping
  public ResponseEntity<Post> createPost(@RequestBody PostPostRequestBody postPostRequestBody) { // RequestBody 어노테이션이 동작하려면 파라미터가 없는 생성자가 필요함
    var post = postService.createPost(postPostRequestBody);

    return ResponseEntity.ok(post);
  }


  @PatchMapping("/{postId}")
  public ResponseEntity<Post> updatePost(@PathVariable Long postId, @RequestBody
      PostPatchRequestBody postPatchRequestBody) {

    var post = postService.updatePost(postId, postPatchRequestBody);

    return ResponseEntity.ok(post);
  }

  @DeleteMapping("/{postId}")
  public ResponseEntity<Void> deletePost(@PathVariable Long postId) {

    postService.deletePost(postId);

    return ResponseEntity.noContent().build();
  }


}
