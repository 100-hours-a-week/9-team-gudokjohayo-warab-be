package store.warab.dto;

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
  private Long id;
  private String nickname;
  private String discordLink;
  private Set<Long> categoryIds;
}
