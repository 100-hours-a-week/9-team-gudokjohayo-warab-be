package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import java.util.stream.Collectors;
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

  private Set<CategoryResponseDto> categorys;

  public static UserProfileResponseDto fromEntity(User user) {
    Set<Category> categoryEntity = user.getCategories();

    Set<CategoryResponseDto> categories =
        categoryEntity.stream()
            .map(category -> new CategoryResponseDto(category.getId(), category.getCategoryName()))
            .collect(Collectors.toSet());

    return new UserProfileResponseDto(user.getNickname(), user.getDiscordLink(), categories);
  }
}
