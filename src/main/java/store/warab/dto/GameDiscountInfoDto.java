package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameDiscountInfoDto {
  @JsonProperty("current_price")
  private Integer currentPrice;

  @JsonProperty("discount_info")
  private List<PlatformDiscountInfoDto> discountInfo;
}
