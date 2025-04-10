package store.warab.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.warab.entity.DiscordLink;

@Repository
public interface DiscordLinkRepository extends JpaRepository<DiscordLink, Long> {
  Optional<DiscordLink> findByUserIdAndGameIdAndDeletedAtIsNull(Long userId, Long gameId);

  boolean existsByUserIdAndGameIdAndDeletedAtIsNull(Long userId, Long gameId);

  List<DiscordLink> findByGameIdAndDeletedAtIsNull(Long gameId);

  List<DiscordLink> findByUserIdAndDeletedAtIsNull(Long userId);

  @Query(
      "SELECT COUNT(d) > 0 FROM DiscordLink d WHERE d.discordUrl = :discordUrl AND d.deletedAt IS NULL")
  boolean existsByDiscordUrlAndDeletedAtIsNull(@Param("discordUrl") String discordUrl);
}
