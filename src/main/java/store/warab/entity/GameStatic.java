package store.warab.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
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
  private String original_title;
  private String description;
  private String release_date;
  private String publisher;
  private String developer;
  private String thumbnail;
  private Integer play_mode;
  private Integer price;

  // 🔹 FK를 갖는 쪽(GameDynamic)에서 관계를 설정하므로 mappedBy 사용
  @OneToOne(
      mappedBy = "game_static",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      optional = true)
  private GameDynamic game_dynamic;

  // ✅ 다대다 관계 매핑: 중간 테이블 (game_category) 사용
  @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<GameCategory> game_categories;

  // ✅ 카테고리 목록을 편하게 가져오는 메서드 추가
  public List<Category> getCategories() {
    return game_categories.stream().map(GameCategory::getCategory).toList();
  }

  // ✅ getter 추가
  public GameDynamic getGameDynamic() {
    return game_dynamic;
  }
}
