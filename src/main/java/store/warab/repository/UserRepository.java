package store.warab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.warab.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  @Query("SELECT u.nickname FROM User u WHERE u.nickname LIKE :pattern ORDER BY u.nickname DESC")
  Optional<String> findLatestUserNickname(@Param("pattern") String pattern);
  Optional<User> findByKakaoId(String kakao_id);
  
  boolean existsByDiscordLink(String discordLink);

  boolean existsByNickname(String nickname);
}
