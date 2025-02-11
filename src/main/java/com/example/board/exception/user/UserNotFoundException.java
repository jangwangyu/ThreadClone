package com.example.board.exception.user;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends ClientErrorException {

  public UserNotFoundException() {
    super(HttpStatus.NOT_FOUND, "User not Found");
  }

  public UserNotFoundException(String username) {
    super(HttpStatus.NOT_FOUND, "User with userId" + username + " not Found");
  }

}
