package store.warab.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import store.warab.entity.GameVideo;

@Getter
public class GameVideoDto {
  private String thumbnail;
  private String title;
  private long views;
  private LocalDateTime upload_date;
  private String channel_thumbnail;
  private String channel_title;

  public GameVideoDto(GameVideo gameVideo) {
    this.thumbnail = gameVideo.getThumbnail();
    this.title = gameVideo.getTitle();
    this.views = gameVideo.getViews();
    this.upload_date = gameVideo.getUpload_date();
    this.channel_thumbnail = gameVideo.getChannel_profile_image();
    this.channel_title = gameVideo.getChannel_name();
  }
}
