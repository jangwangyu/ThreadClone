package com.example.board.controller;

import com.example.board.model.entity.UserEntity;
import com.example.board.model.post.Post;
import com.example.board.model.reply.Reply;
import com.example.board.model.user.Follower;
import com.example.board.model.user.LikedUser;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserLoginRequestBody;
import com.example.board.model.user.UserPatchRequestBody;
import com.example.board.model.user.UserSignUpRequestBody;
import com.example.board.model.user.User;
import com.example.board.service.PostService;
import com.example.board.service.ReplyService;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @Autowired
  ReplyService replyService;

  @GetMapping
  public ResponseEntity<List<User>> getUsers(@RequestParam(required = false) String query, Authentication authentication) {
    var users = userService.getUsers(query,(UserEntity) authentication.getPrincipal());
    return ResponseEntity.ok(users);
  }

  @GetMapping("/{username}")
  public ResponseEntity<User> getUser(@PathVariable String username, Authentication authentication) {
    var user = userService.getUser(username,(UserEntity) authentication.getPrincipal());
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

  // GET user(본인)이 올린 게시물's 조회
  @GetMapping("/{username}/posts")
  public ResponseEntity<List<Post>> getpostsByUsername(@PathVariable String username, Authentication authentication) {
    var posts = postService.getPostsByUsername(username, (UserEntity) authentication.getPrincipal());
    return ResponseEntity.ok(posts);
  }

  @PostMapping("/{username}/follows")
  public ResponseEntity<User> follow(@PathVariable String username,Authentication authentication) { // 유저 정보가 필요함

    var user =  userService.follow(username,(UserEntity) authentication.getPrincipal());

    return ResponseEntity.ok(user);
  }

  @DeleteMapping("/{username}/follows")
  public ResponseEntity<User> unfollow(@PathVariable String username,Authentication authentication) { // 유저 정보가 필요함

    var user =  userService.unfollow(username,(UserEntity) authentication.getPrincipal());

    return ResponseEntity.ok(user);
  }

  @GetMapping("/{username}/followers")
  public ResponseEntity<List<Follower>> getFollowersByUser(@PathVariable String username, Authentication authentication) { // 유저 정보가 필요함

    var followers =  userService.getFollowersByUsername(username, (UserEntity) authentication.getPrincipal());

    return ResponseEntity.ok(followers);
  }

  @GetMapping("/{username}/followings")
  public ResponseEntity<List<User>> getFollowingsByUser(@PathVariable String username, Authentication authentication) { // 유저 정보가 필요함

    var followings =  userService.getFollowingsByUsername(username, (UserEntity) authentication.getPrincipal());

    return ResponseEntity.ok(followings);
  }

  @GetMapping("/{username}/liked-users")
  public ResponseEntity<List<LikedUser>> getLikedUsersByUser(@PathVariable String username, Authentication authentication) { // 유저 정보가 필요함

    var likedUsers =
        userService.getLikedUsersByUser(username, (UserEntity) authentication.getPrincipal());

    return ResponseEntity.ok(likedUsers);
  }

  @GetMapping("/{username}/replies")
  public ResponseEntity<List<Reply>> getRepliesByUser(@PathVariable String username) {

    var replies = replyService.getRepliesByUser(username);

    return ResponseEntity.ok(replies);
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
