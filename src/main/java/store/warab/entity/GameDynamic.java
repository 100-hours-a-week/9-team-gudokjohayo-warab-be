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

  @Id private Long game_id; // game_static의 ID와 동일한 값을 가짐

  private Integer rating;
  private Integer active_players;
  private Integer lowest_platform;
  private Integer lowest_price;
  private Integer history_lowest_Price;
  private LocalDateTime updated_at;
  private Long total_reviews;
  private Boolean on_sale;

  // 🔹 game_static.id를 참조하는 FK 설정
  @OneToOne
  @JoinColumn(name = "game_id", referencedColumnName = "id")
  private GameStatic game_static;

  public GameStatic getGameStatic() {
    return game_static;
  }
}
