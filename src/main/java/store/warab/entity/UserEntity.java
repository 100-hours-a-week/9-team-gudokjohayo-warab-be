package store.warab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 12)
  private String nickname;

  private String discordLink;

  @ManyToMany
  @JoinTable(
      name = "user_category",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "category_id"))
  private Set<CategoryEntity> categories = new HashSet<>();

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;

  private LocalDateTime deletedAt;

  @Builder
  public UserEntity(String nickname, String discordLink) {
    this.nickname = nickname;
    this.discordLink = discordLink;
  }

  @Builder
  public UserEntity(String nickname) {
    this.nickname = nickname;
  }

  @Builder
  public UserEntity(String nickname, String discordLink, Set<CategoryEntity> categories) {
      this.nickname = nickname;
      this.discordLink = discordLink;
      this.categories = categories;
  }
}
