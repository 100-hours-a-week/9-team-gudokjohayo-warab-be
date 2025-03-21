package store.warab.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "category")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)

  private Long id;

  @Column(name = "category_name",nullable = false, length = 100)
  private String categoryName;
}
