package store.warab.dto;

import lombok.Getter;
import lombok.Setter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

@Getter
@Setter
public class GameSearchResponseDTO {
  private Long id;
  private String title;
    private String thumbnail;
  private Integer price;
  private Integer lowestPrice;

  public GameSearchResponseDTO(GameStatic gameStatic, GameDynamic gameDynamic) {
    this.id = gameStatic.getId();
    this.title = gameStatic.getTitle();
    this.thumbnail = gameStatic.getThumbnail();
    this.price = gameStatic.getPrice();

    if (gameDynamic != null) {
      this.lowestPrice = gameDynamic.getLowest_price();
    }
  }
}
