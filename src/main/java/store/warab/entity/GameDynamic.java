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

  @Id private Long gameId; // game_static의 ID와 동일한 값을 가짐

  private Integer rating;
  private Integer activePlayers;
  private String lowestPlatform;
  private Integer lowestPrice;
  private Integer historyLowestPrice;
  private LocalDateTime updatedAt;

  // 🔹 game_static.id를 참조하는 FK 설정
  @OneToOne
  @JoinColumn(name = "game_id", referencedColumnName = "id")
  private GameStatic gameStatic;

  public GameStatic getGameStatic() {
    return gameStatic;
  }
}
