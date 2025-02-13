package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserLoginRequestBody;
import com.example.board.model.user.UserPatchRequestBody;
import com.example.board.model.user.UserSignUpRequestBody;
import com.example.board.model.user.User;
import com.example.board.service.PostService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  @Autowired
  UserService userService;

  @Autowired
  PostService postService;

  @GetMapping
  public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query) {
    var users = userService.getUsers(query);
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{username}")
  public ResponseEntity<User> getUser(@PathVariable String username) {
    var user = userService.getUser(username);
    return ResponseEntity.ok(user);
  }

  @PatchMapping("/{username}")
  public ResponseEntity<User> updateUser(
      @PathVariable String username,
      @RequestBody UserPatchRequestBody requestBody,
      Authentication authentication) {

    var user = userService.updateUser(username, requestBody,(UserEntity) authentication.getPrincipal());
    return ResponseEntity.ok(user);
  }

  // GET /users/{username}/posts
  @GetMapping("/{username}/posts")
  public ResponseEntity<List<Post>> getpostsByUsername(@PathVariable String username) {
    var posts = postService.getPostsByUsername(username);
    return ResponseEntity.ok(posts);
  }

  @PostMapping
  public ResponseEntity<User> signUp(@Valid @RequestBody UserSignUpRequestBody userSignUpReqeustBody) {
    var user = userService.signUp(
        userSignUpReqeustBody.username(),
        userSignUpReqeustBody.password()
    );
    return ResponseEntity.ok(user);
    // return new ResponseEntity<>(user, HttpStatus.OK);
  }


  @PostMapping("/authenticate") // 사용자 인증
  public ResponseEntity<UserAuthenticationResponse> authenticate(
      @Valid @RequestBody UserLoginRequestBody userLoginRequestBody) {
    var response = userService.authenticate(
        userLoginRequestBody.username(),
        userLoginRequestBody.password()
    );
    return ResponseEntity.ok(response);
  }
}
