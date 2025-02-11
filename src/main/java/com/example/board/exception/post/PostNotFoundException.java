package com.example.board.exception.post;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class PostNotFoundException extends ClientErrorException {

  public PostNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Post not Found");
  }

  public PostNotFoundException(Long PostId) {
    super(HttpStatus.NOT_FOUND, "Post with postId" + PostId + " not Found");
  }

  public PostNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
