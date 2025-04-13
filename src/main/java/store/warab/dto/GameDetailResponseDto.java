package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
public class GameDetailResponseDto {
  private String title;
  private String thumbnail;
  private Integer price;
  private Integer lowestPrice;
  private String description;
  private String releaseDate;
  private String developer;
  private String publisher;
  private Integer rating;
  private Boolean singlePlay;
  private Boolean multiPlay;
  private Integer recentPlayer;
  private LocalDateTime updatedAt;
  private List<String> categories;

  public GameDetailResponseDto(GameStatic gameStatic, GameDynamic gameDynamic) {
    this.title = gameStatic.getTitle();
    this.thumbnail = gameStatic.getThumbnail();
    this.price = gameStatic.getPrice();
    this.description = gameStatic.getDescription();
    this.releaseDate = gameStatic.getReleaseDate();
    this.developer = gameStatic.getDeveloper();
    this.publisher = gameStatic.getPublisher();
    this.categories =
        gameStatic.getGame_categories().stream()
            .map(gc -> gc.getCategory().getCategoryName())
            .collect(Collectors.toList());
    this.singlePlay = gameStatic.getIsSinglePlay();
    this.multiPlay = gameStatic.getIsMultiplay();
    if (gameDynamic != null) {
      this.lowestPrice = gameDynamic.getLowestPrice();
      this.rating = gameDynamic.getRating();
      this.recentPlayer = gameDynamic.getActivePlayers();
      this.updatedAt = gameDynamic.getUpdatedAt();
    }
  }
}
