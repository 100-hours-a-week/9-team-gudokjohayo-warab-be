package store.warab.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "game_category")
@Getter
public class GameCategory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // ✅ GameStatic (게임 정보) 연결
  @ManyToOne
  @JoinColumn(name = "game_id")
  private GameStatic game;

  // ✅ Category (카테고리 정보) 연결
  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

}
