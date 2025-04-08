package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LowestPriceLinkDto {
  private String platform;

  @JsonProperty("store_url")
  private String storeUl;
}
