package com.example.board.exception;

import com.example.board.model.error.ClientErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice //AOP를 적용해 예외를 전역적으로 처리할 수 있는 어노테이션
public class GlobalExceptionHandler {

  @ExceptionHandler(ClientErrorException.class)
  public ResponseEntity<ClientErrorResponse> handleClientErrorException(ClientErrorException e) {
    return new
        ResponseEntity<>(new ClientErrorResponse(e.getStatus(), e.getMessage()), e.getStatus());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ClientErrorResponse> handleClientErrorException
      (MethodArgumentNotValidException e) {
    var errorMessages =
        e.getFieldErrors().stream()
            .map(fieldError -> (fieldError.getField() + ": " + fieldError.getDefaultMessage()))
            .toList()
            .toString();

    return new ResponseEntity<>(
        new ClientErrorResponse(HttpStatus.BAD_REQUEST, errorMessages), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ClientErrorResponse> handleClientErrorException(HttpMessageNotReadableException e) {
    return new ResponseEntity<>(
        new ClientErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ClientErrorResponse> handleRuntimeException(RuntimeException e) {
    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ClientErrorResponse> handleException(Exception e) {
    return ResponseEntity.internalServerError().build();
  }

}


