package store.warab.repository;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.GameStatic;

@Repository
public interface GameStaticRepository extends JpaRepository<GameStatic, Long> {

  @EntityGraph(attributePaths = {"gameDynamic"}) // ✅ N+1 문제 해결: gameDynamic을 한 번에 가져오기
  List<GameStatic> findAll();
}
