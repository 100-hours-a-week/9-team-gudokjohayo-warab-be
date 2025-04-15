package store.warab.entity;

import jakarta.persistence.*;
import java.util.List;
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

  @Column(name = "original_title")
  private String originalTitle;

  private String description;

  @Column(name = "release_date")
  private String releaseDate;

  private String publisher;
  private String developer;
  private String thumbnail;
  private Integer price;

  @Column(name = "is_singleplay")
  private Boolean isSinglePlay;

  @Column(name = "is_multiplay")
  private Boolean isMultiplay;

  // 🔹 FK를 갖는 쪽(GameDynamic)에서 관계를 설정하므로 mappedBy 사용
  @OneToOne(
      mappedBy = "gameStatic",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      optional = true)
  private GameDynamic gameDynamic;

  // ✅ 다대다 관계 매핑: 중간 테이블 (game_category) 사용
  @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<GameCategory> gameCategories;

  public List<GameCategory> getGame_categories() {
    if (gameCategories == null) {
      return List.of(); // ✅ Null 방지 (빈 리스트 반환)
    }
    return gameCategories;
  }

  // ✅ getter 추가
  public GameDynamic getGameDynamic() {
    return gameDynamic;
  }
}
