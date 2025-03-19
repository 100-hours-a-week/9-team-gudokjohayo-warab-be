package store.warab.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.warab.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // ✅ 존재하는 카테고리 ID만 조회하는 쿼리 추가
    @Query("SELECT c.id FROM Category c WHERE c.id IN :category_ids")
    List<Long> findValidCategoryIds(@Param("category_ids") List<Long> category_ids);
}
