package store.warab.controller;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.warab.dto.CategoryListResponseDto;
import store.warab.service.CategoryService;

@RestController
@RequestMapping("/api/v1/category")
// @CrossOrigin(origins = "http://localhost:3000") // 프론트 3000번 포트 허용
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  // 모든 카테고리 조회
  @GetMapping
  public ResponseEntity<Set<CategoryListResponseDto>> getAllCategories() {
    Set<CategoryListResponseDto> result =
        categoryService.getAllCategories().stream()
            .map(
                category ->
                    new CategoryListResponseDto(
                        category.getId(), // id가 gameId가 아니라면 변수명도 맞게 수정
                        category.getCategoryName()))
            .collect(Collectors.toSet());

    return ResponseEntity.ok(result);
  }
}
