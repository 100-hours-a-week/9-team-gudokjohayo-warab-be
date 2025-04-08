package store.warab.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.Platform;

@Repository
public interface PlatformRepository extends JpaRepository<Platform, Long> {}
