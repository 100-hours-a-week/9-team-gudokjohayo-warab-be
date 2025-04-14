package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserProfileResponseDto {
  private String nickname;

  private Set<CategoryResponseDto> categorys;

  public static UserProfileResponseDto fromEntity(User user) {
    Set<Category> categoryEntity = user.getCategories();

    Set<CategoryResponseDto> categories =
        categoryEntity.stream()
            .map(category -> new CategoryResponseDto(category.getId(), category.getCategoryName()))
            .collect(Collectors.toSet());

    return new UserProfileResponseDto(user.getNickname(), categories);
  }
}
