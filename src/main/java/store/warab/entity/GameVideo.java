package store.warab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Entity
@Table(name = "video")
public class GameVideo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  private GameStatic gameStatic;

  @Column(name = "video_id", nullable = false)
  private String videoId;

  @Column(name = "title", nullable = false)
  private String title;

  @Column(name = "thumbnail", nullable = false)
  private String thumbnail;

  @Column(name = "views", nullable = false)
  private Long views;

  @Column(name = "upload_date", nullable = false)
  private LocalDateTime uploadDate;

  @Column(name = "channel_profile_image", nullable = false)
  private String channelProfileImage;

  @Column(name = "channel_name", nullable = false)
  private String channelName;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
