package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

@Getter
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameSearchResponseDto {
  private Long gameId;
  private String title;
  private String thumbnail;
  private Integer price;
  private Integer lowestPrice;

  public GameSearchResponseDto(GameStatic gameStatic, GameDynamic gameDynamic) {
    this.gameId = gameStatic.getId();
    this.title = gameStatic.getTitle();
    this.thumbnail = gameStatic.getThumbnail();
    this.price = gameStatic.getPrice();

    if (gameDynamic != null) {
      this.lowestPrice = gameDynamic.getLowestPrice();
    }
  }
}
