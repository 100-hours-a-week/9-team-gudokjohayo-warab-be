package store.warab.id;

import java.io.Serializable;
import java.util.Objects;

public class CurrentPriceByPlatformId implements Serializable {

  private Long gameStatic;
  private Long platform;

  public CurrentPriceByPlatformId() {}

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CurrentPriceByPlatformId)) return false;
    CurrentPriceByPlatformId that = (CurrentPriceByPlatformId) o;
    return Objects.equals(gameStatic, that.gameStatic) && Objects.equals(platform, that.platform);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gameStatic, platform);
  }
}
