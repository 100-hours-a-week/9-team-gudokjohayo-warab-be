package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import store.warab.entity.CurrentPriceByPlatform;
import store.warab.entity.Platform;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PlatformDiscountInfoDto {
  private String platform;
  private Integer discountPrice;
  private Integer discountRate;
  private String storeUrl;

  public PlatformDiscountInfoDto(CurrentPriceByPlatform currentPriceByPlatform) {
    Platform platform = currentPriceByPlatform.getPlatform();
    this.platform = platform.getPlatformName();
    this.discountPrice = currentPriceByPlatform.getDiscountPrice();
    this.discountRate = currentPriceByPlatform.getDiscountRate();
    this.storeUrl = currentPriceByPlatform.getUrl();
  }
}
