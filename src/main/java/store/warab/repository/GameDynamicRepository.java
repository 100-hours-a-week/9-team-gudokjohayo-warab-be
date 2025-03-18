package store.warab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.GameDynamic;

@Repository
public interface GameDynamicRepository extends JpaRepository<GameDynamic, Long> {}
