package store.warab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "comment")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer commentId;

  /** TODO: user_id -> 실제 User 엔티티와의 @ManyToOne 관계 설정 예정 */
  @Column(name = "user_id", nullable = false)
  private Integer userId;

  /** TODO: game_id -> 실제 Game 엔티티와의 @ManyToOne 관계 설정 예정 */
  @Column(name = "game_id", nullable = false)
  private Integer gameId;

  @Column(length = 100, nullable = false)
  private String content; // varchar(100)

  @Column(
      name = "created_at",
      insertable = false,
      updatable = false,
      columnDefinition = "timestamp default now()")
  private LocalDateTime createdAt;

  @Column(
      name = "updated_at",
      insertable = false,
      updatable = false,
      columnDefinition = "timestamp default now()")
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt; // null 가능

  @PreUpdate
  public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
