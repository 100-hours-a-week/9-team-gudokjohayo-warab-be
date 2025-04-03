package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.warab.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthUserResponseDto {
  private Long id;
  private String nickname;

  @JsonProperty("is_authenticated")
  private boolean isAuthenticated;

  public static AuthUserResponseDto fromEntity(User user) {
    return new AuthUserResponseDto(user.getId(), user.getNickname(), true);
  }
}
