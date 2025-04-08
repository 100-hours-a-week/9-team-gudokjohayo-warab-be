package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import store.warab.entity.CurrentPriceByPlatform;
import store.warab.entity.Platform;

@Getter
public class PlatformDiscountInfoDto {
  private String platform;

  @JsonProperty("discount_price")
  private Integer discountPrice;

  @JsonProperty("discount_rate")
  private Integer discountRate;

  @JsonProperty("store_url")
  private String storeUrl;

  public PlatformDiscountInfoDto(CurrentPriceByPlatform currentPriceByPlatform) {
    Platform platform = currentPriceByPlatform.getPlatform();
    this.platform = platform.getPlatformName();
    this.discountPrice = currentPriceByPlatform.getDiscountPrice();
    this.discountRate = currentPriceByPlatform.getDiscountRate();
    this.storeUrl = currentPriceByPlatform.getUrl();
  }
}
