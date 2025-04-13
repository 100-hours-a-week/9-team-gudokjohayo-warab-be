package store.warab.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.GameStatic;
import store.warab.entity.GameVideo;

@Repository
public interface GameVideoRepository extends JpaRepository<GameVideo, Long> {
  List<GameVideo> findByGameStatic(GameStatic game);
}
