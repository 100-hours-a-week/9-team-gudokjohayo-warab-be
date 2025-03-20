package store.warab.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 특정 게임에 해당하는 모든 댓글 조회
    List<Comment> findByGameId(Integer gameId);
}
