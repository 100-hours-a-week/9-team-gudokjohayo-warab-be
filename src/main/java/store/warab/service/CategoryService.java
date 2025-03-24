package store.warab.service;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.warab.entity.Category;
import store.warab.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;

  public Set<Category> getAllCategories() {
    return new HashSet<>(categoryRepository.findAll());
  }
}
