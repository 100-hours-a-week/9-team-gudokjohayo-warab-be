package store.warab.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

  // 특정 게임에 해당하는 모든 댓글 조회
  List<Comment> findByGameId(Integer gameId);
}
