package store.warab.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.warab.entity.CategoryEntity;
import store.warab.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserDto {
  private Long id;
  private String nickname;
  private String discordLink;
  private Set<CategoryEntity> categories;

  public static UserDto fromEntity(User user) {
    return new UserDto(
        user.getId(), user.getNickname(), user.getDiscordLink(), user.getCategories());
  }
}
