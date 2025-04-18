package store.warab.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.warab.common.util.ApiResponse;
import store.warab.dto.CategoryResponseDto;
import store.warab.service.CategoryService;

@RestController
@RequestMapping("/api/v1/games/category")
@RequiredArgsConstructor
public class CategoryController {

  private final CategoryService categoryService;

  // 모든 카테고리 조회
  @GetMapping
  public ResponseEntity<ApiResponse> getAllCategories() {
    Set<CategoryResponseDto> categorys =
        categoryService.getAllCategories().stream()
            .map(
                category ->
                    new CategoryResponseDto(
                        category.getId(), // id가 gameId가 아니라면 변수명도 맞게 수정
                        category.getCategoryName()))
            .collect(Collectors.toSet());
    Map<String, Object> data = new HashMap<>();
    data.put("categorys", categorys);
    return ResponseEntity.ok(new ApiResponse("category_list_inquiry_success", data));
  }
}
