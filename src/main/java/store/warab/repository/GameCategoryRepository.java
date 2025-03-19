package store.warab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.GameCategory;

@Repository
public interface GameCategoryRepository extends JpaRepository<GameCategory, Long> {}
