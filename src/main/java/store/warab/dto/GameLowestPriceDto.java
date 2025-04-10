package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.warab.entity.GameDynamic;

@Getter
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameLowestPriceDto {
  private int historyLowestPrice;

  public GameLowestPriceDto(GameDynamic gameDynamic) {
    this.historyLowestPrice = gameDynamic.getHistoryLowestPrice();
  }
}
