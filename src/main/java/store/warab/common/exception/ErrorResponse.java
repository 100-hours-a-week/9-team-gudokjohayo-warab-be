package store.warab.common.exception;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
  private final LocalDateTime timestamp = LocalDateTime.now();
  private final int status;
  private final String error;
  private final String message;
  private final String path;
}
