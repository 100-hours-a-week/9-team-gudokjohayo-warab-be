package store.warab.dto;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.warab.entity.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserUpdateResponseDto {
  private Long id;
  private String nickname;
  private String discordLink;
  private Set<Long> categoriesSet;

  public UserUpdateResponseDto(User user) {
    this.id = user.getId();
    this.nickname = user.getNickname();
    this.discordLink = user.getDiscordLink();
    //        this.categories = user.getCategories();
  }
}
