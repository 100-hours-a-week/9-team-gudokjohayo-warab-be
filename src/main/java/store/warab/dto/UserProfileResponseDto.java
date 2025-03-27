package store.warab.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.warab.entity.Category;
import store.warab.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserProfileResponseDto {
  private String nickname;

    @JsonProperty("discord_link")
  private String discordLink;

  private Set<Category> categories;

  public static UserProfileResponseDto fromEntity(User user) {
    return new UserProfileResponseDto(
        user.getNickname(), user.getDiscordLink(), user.getCategories());
  }
}
