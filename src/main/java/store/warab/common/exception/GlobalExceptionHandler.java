package store.warab.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 400 Error
  @ExceptionHandler(BadRequestException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ErrorResponse> handleBadRequestException(
      BadRequestException e, HttpServletRequest request) {
    log.error("400 Error (Validation Failed): ", e.getMessage());

    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message(e.getMessage())
            .path(request.getRequestURI())
            .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // 404 Error
  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      NotFoundException e, HttpServletRequest request) {
    log.error("404 Error: ", e.getMessage());

    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.NOT_FOUND.value())
            .error(HttpStatus.NOT_FOUND.getReasonPhrase())
            .message("not_found")
            .path(request.getRequestURI())
            .build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  // 500 Error
  @ExceptionHandler(InternalServerException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ErrorResponse> handleInternalServerException(InternalServerException e, HttpServletRequest request) {
    log.error("서버 에러 발생: ", e);

    ErrorResponse response =
        ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .message("internal_server_error")
            .path(request.getRequestURI())
            .build();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
