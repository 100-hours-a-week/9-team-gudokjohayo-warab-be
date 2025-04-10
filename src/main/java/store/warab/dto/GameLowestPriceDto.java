package store.warab.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import store.warab.entity.GameDynamic;

@Getter
@NoArgsConstructor
public class GameLowestPriceDto {
  private int history_lowest_price;

  public GameLowestPriceDto(GameDynamic gameDynamic) {
    this.history_lowest_price = gameDynamic.getHistoryLowestPrice();
  }
}
