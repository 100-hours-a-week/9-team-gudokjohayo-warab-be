package store.warab.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

@Entity
@Table(name = "game_discord_channel")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscordLink {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  private GameStatic game;

  @Column(name = "url", nullable = false)
  private String discordUrl;

  @Column(name = "channel_name", length = 255, nullable = false)
  private String channelName;

  @Column(name = "channel_description", length = 255)
  private String channelDescription;

  @Column(name = "member_count")
  private Integer memberCount;

  @Column(name = "channel_icon", length = 255)
  private String channelIcon;

  @Column(name = "expired_at", length = 255)
  private LocalDate expiredAt;

  @CreatedDate
  @Column(name = "created_at", updatable = false, nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
