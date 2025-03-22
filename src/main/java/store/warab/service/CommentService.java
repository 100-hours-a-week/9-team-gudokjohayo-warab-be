package store.warab.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.warab.entity.Comment;
import store.warab.repository.CommentRepository;
import store.warab.repository.UserRepository;
import store.warab.repository.GameStaticRepository;
import store.warab.entity.User;
import store.warab.entity.GameStatic;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final GameStaticRepository gameStaticRepository;

    public CommentService(CommentRepository commentRepository,
                         UserRepository userRepository,
                         GameStaticRepository gameStaticRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.gameStaticRepository = gameStaticRepository;
    }

    // 댓글 작성
    public Comment createComment(String nickname, Long gameId, String content) {
        // 입력값 검증
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
        if (content.length() > 100) {
            throw new IllegalArgumentException("댓글은 100자를 초과할 수 없습니다.");
        }

        User user = userRepository.findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: " + nickname));
        
        GameStatic game = gameStaticRepository.findById(gameId)
            .orElseThrow(() -> new ResourceNotFoundException("게임을 찾을 수 없습니다: " + gameId));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setGameStatic(game);
        comment.setContent(content.trim());

        return commentRepository.save(comment);
    }

    // 특정 게임의 댓글 목록 (삭제되지 않은 댓글만)
    @Transactional(readOnly = true)
    public List<Comment> getCommentsByGameId(Long gameId) {
        return commentRepository.findByGameStaticIdAndDeletedAtIsNull(gameId);
    }

    // 특정 댓글 조회
    @Transactional(readOnly = true)
    public Comment getComment(Long commentId) {
        return commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("댓글을 찾을 수 없습니다: " + commentId));
    }

    // 댓글 수정
    public Comment updateComment(Long gameId, Long commentId, String newContent) {
        // 입력값 검증
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
        if (newContent.length() > 100) {
            throw new IllegalArgumentException("댓글은 100자를 초과할 수 없습니다.");
        }

        Comment comment = commentRepository.findByIdAndGameStaticIdAndDeletedAtIsNull(commentId, gameId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게임의 댓글을 찾을 수 없습니다."));
        
        comment.setContent(newContent.trim());
        return commentRepository.save(comment);
    }

    // 댓글 삭제 (소프트 딜리트)
    public void deleteComment(Long gameId, Long commentId) {
        Comment comment = commentRepository.findByIdAndGameStaticIdAndDeletedAtIsNull(commentId, gameId)
            .orElseThrow(() -> new ResourceNotFoundException("해당 게임의 댓글을 찾을 수 없습니다."));
        
        comment.setDeletedAt(LocalDateTime.now());
        commentRepository.save(comment);
    }
}
