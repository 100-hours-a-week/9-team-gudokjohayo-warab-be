package store.warab.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.CurrentPriceByPlatform;

@Repository
public interface CurrentPriceByPlatformRepository
    extends JpaRepository<CurrentPriceByPlatform, Long> {
  List<CurrentPriceByPlatform> findAllByGameStatic_Id(Long gameId);
}
