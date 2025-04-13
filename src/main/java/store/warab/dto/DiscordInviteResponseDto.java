package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscordInviteResponseDto {
  @JsonProperty("expires_at")
  private LocalDate expiresAt;

  @JsonProperty("guild")
  private Guild guild;

  @JsonProperty("approximate_member_count")
  private Integer approximateMemberCount;

  @Getter
  @Setter
  public static class Guild {
    private String id;
    private String name;
    private String description;
    private String icon;
  }
}
