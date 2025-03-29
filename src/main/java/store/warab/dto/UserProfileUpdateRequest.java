package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserProfileUpdateRequest {
  @NotNull(message = "nickname is required")
  private String nickname;

  @JsonProperty("discord")
  private String discordLink;

  @JsonProperty("category")
  @NotNull(message = "category is required")
  private Set<Long> categoryIds;
}
