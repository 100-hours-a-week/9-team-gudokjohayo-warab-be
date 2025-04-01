package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

@Getter
public class GameDetailResponseDto {
  private String title;
  private String thumbnail;
  private Integer price;
  private Integer lowest_price;
  private String description;
  private String release_date;
  private String developer;
  private String publisher;
  private Integer rating;

  @JsonProperty("single_play")
  private Boolean singlePlay;

  @JsonProperty("multi_play")
  private Boolean multiPlay;

  private Integer recent_player;
  private LocalDateTime updated_at;
  private List<String> categories;

  public GameDetailResponseDto(GameStatic gameStatic, GameDynamic gameDynamic) {
    this.title = gameStatic.getTitle();
    this.thumbnail = gameStatic.getThumbnail();
    this.price = gameStatic.getPrice();
    this.description = gameStatic.getDescription();
    this.release_date = gameStatic.getRelease_date();
    this.developer = gameStatic.getDeveloper();
    this.publisher = gameStatic.getPublisher();
    this.categories =
        gameStatic.getGame_categories().stream()
            .map(gc -> gc.getCategory().getCategoryName())
            .collect(Collectors.toList());
    this.singlePlay = gameStatic.getIsSinglePlay();
    this.multiPlay = gameStatic.getIsMultiplay();
    if (gameDynamic != null) {
      this.lowest_price = gameDynamic.getLowest_price();
      this.rating = gameDynamic.getRating();
      this.recent_player = gameDynamic.getActive_players();
      this.updated_at = gameDynamic.getUpdated_at();
    }
  }
}
