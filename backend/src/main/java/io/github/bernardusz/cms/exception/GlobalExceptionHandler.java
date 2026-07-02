package io.github.bernardusz.cms.exception;

import io.github.bernardusz.cms.exception.dto.ErrorResponse;
import io.github.bernardusz.cms.exception.exceptions.NotAuthorizedException;
import io.github.bernardusz.cms.exception.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFound.class)
  public ResponseEntity<ErrorResponse> handleUserNotFound(){
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(new ErrorResponse(
        "User not found",
        HttpStatus.NOT_FOUND.value(),
        LocalDateTime.now()
      ));
  }

  @ExceptionHandler(NotAuthorizedException.class)
  public ResponseEntity<ErrorResponse> handleNotAllowed(){
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
      .body(new ErrorResponse(
        "You are not allowed to look here",
        HttpStatus.NOT_FOUND.value(),
        LocalDateTime.now()
      ));
  }

}
