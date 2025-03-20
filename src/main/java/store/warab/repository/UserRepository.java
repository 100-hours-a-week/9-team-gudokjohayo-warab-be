package store.warab.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.warab.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT u.nickname FROM User u WHERE u.nickname LIKE :pattern ORDER BY u.nickname DESC")
  Optional<String> findLatestUserNickname(@Param("pattern") String pattern);
  Optional<User> findByKakaoId(String kakao_id);

  boolean existsByDiscordLink(String discordLink);

  boolean existsByNickname(String nickname);
}
