package store.warab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  boolean existsByDiscordLink(String discordLink);

  boolean existsByNickname(String nickname);
}
