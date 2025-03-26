package store.warab.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainPageResponseDto {
  private String title;
  private List<GameInfoDto> games;

  public MainPageResponseDto(String title, List<GameInfoDto> games) {
    this.title = title;
    this.games = games;
  }
}
