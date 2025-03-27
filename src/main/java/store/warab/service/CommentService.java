package store.warab.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.warab.dto.CommentResponseDto;
import store.warab.entity.Comment;
import store.warab.entity.User;
import store.warab.repository.CommentRepository;
import store.warab.repository.UserRepository;

@Service
@Transactional
public class CommentService {

  private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository) {
    this.commentRepository = commentRepository;
        this.userRepository = userRepository;
  }

  // 댓글 작성
  public Comment createComment(Long userId, Long gameId, String content) {
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

    public List<CommentResponseDto> getCommentDtosByGameId(Integer gameId)
    {
        List<Comment> comments = commentRepository.findByGameId(gameId);
        return comments.stream()
            .map(comment -> {
                User user = userRepository.findById(comment.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
                return new CommentResponseDto(
                    user,comment
                );
            })
            .collect(Collectors.toList());

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
  public void deleteComment(Integer commentId, Long tokenUserId, boolean softDelete) {
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

  public boolean isOwnerOfComment(Long tokenUserId, Integer commentId) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

    return comment.getUserId().equals(tokenUserId);
  }
}
