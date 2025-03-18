package store.warab.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "game_static")
public class GameStatic {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;
  private String originalTitle;
  private String description;
  private String releaseDate;
  private String publisher;
  private String developer;
  private String thumbnail;
  private Integer playerCount;
  private Integer price;

  // 🔹 FK를 갖는 쪽(GameDynamic)에서 관계를 설정하므로 mappedBy 사용
  @OneToOne(
      mappedBy = "gameStatic",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      optional = true)
  private GameDynamic gameDynamic;

  // ✅ getter 추가
  public GameDynamic getGameDynamic() {
    return gameDynamic;
  }
}
