package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

@Getter
@Setter
@NoArgsConstructor
public class GameInfoDto {
  @JsonProperty("game_id")
  private Long gameId;

  private String title;
  private String thumbnail;
  private Integer price;

  @JsonProperty("lowest_price")
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
