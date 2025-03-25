package store.warab.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryListResponseDto {
  private Long category_id;
  private String category_name;
}
