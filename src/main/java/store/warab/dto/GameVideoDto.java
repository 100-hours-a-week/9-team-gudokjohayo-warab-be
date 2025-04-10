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
  private String channel_name;
  private String video_url;

  public GameVideoDto(GameVideo gameVideo) {
    this.thumbnail = gameVideo.getThumbnail();
    this.title = gameVideo.getTitle();
    this.views = gameVideo.getViews();
    this.upload_date = gameVideo.getUploadDate();
    this.channel_thumbnail = gameVideo.getChannelProfileImage();
    this.channel_name = gameVideo.getChannelName();
    this.video_url = "https://youtube.com/watch?v=" + gameVideo.getVideoId();
  }
}
