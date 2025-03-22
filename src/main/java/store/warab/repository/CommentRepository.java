package store.warab.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.warab.entity.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByGameStaticIdAndDeletedAtIsNull(Long gameId);
    Optional<Comment> findByIdAndDeletedAtIsNull(Long commentId);
    Optional<Comment> findByIdAndGameStaticIdAndDeletedAtIsNull(Long commentId, Long gameId);
}
