package store.warab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "game_dynamic")
public class GameDynamic {

  @Id
  @Column(name = "game_id")
  private Long gameId; // game_static의 ID와 동일한 값을 가짐

  private Integer rating;

  @Column(name = "active_players")
  private Integer activePlayers;

  @Column(name = "lowest_platform")
  private Integer lowestPlatform;

  @Column(name = "lowest_price")
  private Integer lowestPrice;

  @Column(name = "history_lowest_price")
  private Integer historyLowestPrice;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @Column(name = "total_reviews")
  private Long totalReviews;

  @Column(name = "on_sale")
  private Boolean onSale;

  // 🔹 game_static.id를 참조하는 FK 설정
  @OneToOne
  @JoinColumn(name = "game_id", referencedColumnName = "id")
  private GameStatic gameStatic;

  public GameStatic getGameStatic() {
    return gameStatic;
  }
}
