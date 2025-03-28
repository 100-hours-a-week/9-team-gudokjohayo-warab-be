package store.warab.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.warab.entity.Comment;
import store.warab.repository.CommentRepository;

@Service
@Transactional
public class CommentService {

  private final CommentRepository commentRepository;

  public CommentService(CommentRepository commentRepository) {
    this.commentRepository = commentRepository;
  }

  // 댓글 작성
  public Comment createComment(Integer userId, Integer gameId, String content) {
    // userId, gameId 검증(존재 여부 등)은 추후 실제 User, Game 매핑 후에 처리
    Comment comment = new Comment();
    comment.setUserId(userId);
    comment.setGameId(gameId);
    comment.setContent(content);

    return commentRepository.save(comment);
  }

  // 특정 게임의 댓글 목록
  @Transactional(readOnly = true)
  public List<Comment> getCommentsByGameId(Integer gameId) {
    return commentRepository.findByGameId(gameId);
  }

  // 특정 댓글
  @Transactional(readOnly = true)
  public Comment getComment(Integer commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다. ID=" + commentId));
  }

  // 댓글 수정
  public Comment updateComment(Integer commentId, String newContent) {
    Comment comment = getComment(commentId);
    comment.setContent(newContent);
    return commentRepository.save(comment);
  }

  // 댓글 삭제
  public void deleteComment(Integer commentId, Long tokenUserId , boolean softDelete) {
    Comment comment = getComment(commentId);

    // 댓글 작성자가 요청한 사용자와 일치하는지 확인
    if (!comment.getUserId().equals(tokenUserId)) {
      throw new IllegalArgumentException("해당 댓글을 삭제할 권한이 없습니다.");
    }

    if (softDelete) {
      // 논리적 삭제: deleted_at 컬럼에 현재 시간 세팅
      comment.setDeletedAt(java.time.LocalDateTime.now());
      commentRepository.save(comment);
    } else {
      // 물리 삭제
      commentRepository.deleteById(commentId);
    }
  }
}
