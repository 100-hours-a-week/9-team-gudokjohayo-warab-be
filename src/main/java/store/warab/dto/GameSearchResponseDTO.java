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
//  private String description;
//  private Integer playerCount;
    private String thumbnail;
  private Integer price;
//  private Integer rating;
//  private Integer activePlayers;
//  private String lowestPlatform;
  private Integer lowestPrice;

  public GameSearchResponseDTO(GameStatic gameStatic, GameDynamic gameDynamic) {
    this.id = gameStatic.getId();
    this.title = gameStatic.getTitle();
//    this.description = gameStatic.getDescription();
//    this.playerCount = gameStatic.getPlayerCount();
      this.thumbnail = gameStatic.getThumbnail();
    this.price = gameStatic.getPrice();

    if (gameDynamic != null) {
//      this.rating = gameDynamic.getRating();
//      this.activePlayers = gameDynamic.getActivePlayers();
//      this.lowestPlatform = gameDynamic.getLowestPlatform();
      this.lowestPrice = gameDynamic.getLowestPrice();
    }
  }
}
