package com.example.board.controller;

import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserLoginRequestBody;
import com.example.board.model.user.UserSignUpRequestBody;
import com.example.board.model.user.User;
import com.example.board.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  @Autowired
  UserService userService;

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
