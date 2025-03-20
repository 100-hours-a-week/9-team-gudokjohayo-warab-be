package store.warab.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
  private String message;
  private Object data;

  public ApiResponse() {}

  public ApiResponse(String messageText, Object data) {
    this.message = messageText;
    this.data = data;
  }
}
