package store.warab.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "kakao_id", length = 255, unique = true)
  private String kakaoId;

  @Column(nullable = false, unique = true, length = 12)
  private String nickname;

  @Column(name = "discord_link", length = 255)
  private String discordLink;

  @ManyToMany
  @JoinTable(
      name = "user_category",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  @Builder.Default
  private Set<Category> categories = new HashSet<>();

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Builder
  public User(String kakaoId, String nickname, String discordLink) {
    this.kakaoId = kakaoId;
    this.nickname = nickname;
    this.discordLink = discordLink;
  }

  @Builder
  public User(String kakaoId, String nickname, String discordLink, Set<Category> categories) {
    this.kakaoId = kakaoId;
    this.nickname = nickname;
    this.discordLink = discordLink;
    this.categories = categories;
  }
}
