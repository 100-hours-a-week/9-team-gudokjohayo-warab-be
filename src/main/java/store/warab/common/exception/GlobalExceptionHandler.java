package store.warab.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      HttpStatus status, String message, String path) {
    ErrorResponse response =
        ErrorResponse.builder()
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(path)
            .build();
    return new ResponseEntity<>(response, status);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequestException(
      BadRequestException e, HttpServletRequest request) {
    log.error("400 Error: {}", e.getMessage());
    return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidTokenException(
      InvalidTokenException e, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.UNAUTHORIZED, e.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbiddenException(
      ForbiddenException e, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.FORBIDDEN, e.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      NotFoundException e, HttpServletRequest request) {
    log.error("404 Error: {}", e.getMessage());
    return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(InternalServerException.class)
  public ResponseEntity<ErrorResponse> handleInternalServerException(
      InternalServerException e, HttpServletRequest request) {
    log.error("500 Error: ", e);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoHandlerFound(
      NoHandlerFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.NOT_FOUND, "요청한 경로가 없습니다.", request.getRequestURI());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleResponseStatus(
      ResponseStatusException ex, HttpServletRequest request) {
    return buildErrorResponse(
        HttpStatus.valueOf(ex.getStatusCode().value()), ex.getReason(), request.getRequestURI());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, HttpServletRequest request) {
    return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> fallback(Exception ex, HttpServletRequest request) {
    log.error("Unhandled exception", ex);
    return buildErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류", request.getRequestURI());
  }
}
