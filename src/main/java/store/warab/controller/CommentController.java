package store.warab.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import store.warab.entity.Comment;
import store.warab.jwt.JWTUtil;
import store.warab.service.AuthService;
import store.warab.service.CommentService;

@RestController
@RequestMapping("/api/v1/comment") // URL
//@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

  private final CommentService commentService;
  private final AuthService authService;

  public CommentController(CommentService commentService, AuthService authService) {

      this.commentService = commentService;
      this.authService = authService;
  }

  /** [POST] /api/v1/comment 새 댓글 생성 */
  @PostMapping
  public Comment createComment(@RequestBody CommentRequest request) {
    return commentService.createComment(
        request.getUserId(), request.getGameId(), request.getContent());
  }

  /** [GET] /api/v1/comment/game/{gameId} 특정 게임에 달린 댓글 목록 */
  @GetMapping("/game/{gameId}")
  public List<Comment> getCommentsByGame(
      @PathVariable Integer gameId) {
    return commentService.getCommentsByGameId(gameId);
  }

  /** [PUT] /api/comment/{commentId} 댓글 수정 */
  @PutMapping("/{commentId}")
  public Comment updateComment(
      @PathVariable Integer commentId,
      @RequestBody CommentRequest request,
      @RequestHeader("Authorization") String token) {

      Long tokenUserId = authService.extractUserId(token);
      authService.verifyUser(tokenUserId, request.getUserId().longValue());

      return commentService.updateComment(commentId, request.getContent());
  }

  /** [DELETE] /api/comment/{commentId} 댓글 삭제(softDelete 여부에 따라 분기) */
  @DeleteMapping("/{commentId}")
  public void deleteComment(
      @PathVariable Integer commentId,
      @RequestParam(defaultValue = "false") boolean softDelete,
      @RequestHeader("Authorization") String token) {

      Long tokenUserId = authService.extractUserId(token);
      commentService.deleteComment(commentId, tokenUserId, softDelete);

}

  /** 댓글 생성/수정 시 받을 DTO */
  public static class CommentRequest {
    private Integer userId;
    private Integer gameId;
    private String content;

    public Integer getUserId() {
      return userId;
    }

    public void setUserId(Integer userId) {
      this.userId = userId;
    }

    public Integer getGameId() {
      return gameId;
    }

    public void setGameId(Integer gameId) {
      this.gameId = gameId;
    }

    public String getContent() {
      return content;
    }

    public void setContent(String content) {
      this.content = content;
    }
  }
}

