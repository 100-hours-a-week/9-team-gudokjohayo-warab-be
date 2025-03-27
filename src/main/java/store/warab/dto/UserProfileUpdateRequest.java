package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  private String nickname;

  @JsonProperty("discord")
  private String discordLink;

  @JsonProperty("categories")
  private Set<Long> categoryIds;
}
