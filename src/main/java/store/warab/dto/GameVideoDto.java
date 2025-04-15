package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import lombok.Getter;
import store.warab.entity.GameVideo;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GameVideoDto {
  private String thumbnail;
  private String title;
  private long views;
  private LocalDateTime uploadDate;
  private String channelThumbnail;
  private String channelName;
  private String videoUrl;

  public GameVideoDto(GameVideo gameVideo) {
    this.thumbnail = gameVideo.getThumbnail();
    this.title = gameVideo.getTitle();
    this.views = gameVideo.getViews();
    this.uploadDate = gameVideo.getUploadDate();
    this.channelThumbnail = gameVideo.getChannelProfileImage();
    this.channelName = gameVideo.getChannelName();
    this.videoUrl = "https://youtube.com/watch?v=" + gameVideo.getVideoId();
  }
}
