package store.warab.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import store.warab.id.CurrentPriceByPlatformId;

@Entity
@Getter
@Setter
@IdClass(CurrentPriceByPlatformId.class)
@Table(name = "current_price_by_platform") // 생략가능
public class CurrentPriceByPlatform {

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id") // CurrentPriceByPlatform 테이블의 컬럼명과 연결
  private GameStatic gameStatic;

  @Id
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "platform_id")
  private Platform platform;

  @Column(name = "discount_rate")
  private Integer discountRate;

  @Column(name = "discount_price")
  private Integer discountPrice;

  @Column(name = "created_at", nullable = false, updatable = false) // 처음 생성 시간 고정. 한 번 생성되면 수정 불가
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  private String url;
}
