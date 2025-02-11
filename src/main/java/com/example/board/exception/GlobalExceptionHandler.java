package com.example.board.exception;

import com.example.board.model.error.ClientErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //AOP를 적용해 예외를 전역적으로 처리할 수 있는 어노테이션
public class GlobalExceptionHandler {

  @ExceptionHandler(ClientErrorException.class)
  public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
    return new
        ResponseEntity<>(new ClientErrorResponse(e.getStatus(), e.getMessage()), e.getStatus());
  }

}
