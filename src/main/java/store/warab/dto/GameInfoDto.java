package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

@Getter
@Setter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameInfoDto {
  private Long gameId;
  private String title;
  private String thumbnail;
  private Integer price;
  private Integer lowestPrice;

  public GameInfoDto(GameStatic gameStatic, GameDynamic gameDynamic) {
    this.gameId = gameStatic.getId();
    this.title = gameStatic.getTitle();
    this.thumbnail = gameStatic.getThumbnail();
    this.price = gameStatic.getPrice();
    if (gameDynamic != null) {
      this.lowestPrice = gameDynamic.getLowestPrice();
    }
  }
}
