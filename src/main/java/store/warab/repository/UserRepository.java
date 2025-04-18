package store.warab.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.warab.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT u.nickname FROM User u WHERE u.nickname LIKE :pattern ORDER BY u.createdAt DESC")
  List<String> findLatestUserNickname(@Param("pattern") String pattern);

  Optional<User> findByKakaoId(String kakaoId);

  boolean existsByNickname(String nickname);

  Optional<User> findByNickname(String nickname);
}
