package com.example.board.exception.reply;

import com.example.board.exception.ClientErrorException;
import org.springframework.http.HttpStatus;

public class ReplyNotFoundException extends ClientErrorException {

  public ReplyNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Reply not Found");
  }

  public ReplyNotFoundException(Long replyId) {
    super(HttpStatus.NOT_FOUND, "Reply with replyId" + replyId + " not Found");
  }

  public ReplyNotFoundException(String message) {
    super(HttpStatus.NOT_FOUND, message);
  }
}
