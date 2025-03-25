package store.warab.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.util.ApiResponse;
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
  public ResponseEntity<ApiResponse> createComment(@RequestBody CommentRequest request) {
      commentService.createComment(
          request.getUserId(), request.getGameId(), request.getContent());
      Map<String, Object> data = null;
      return ResponseEntity
          .status(HttpStatus.CREATED)
          .body(new ApiResponse("comment_create_success", data));
  }

  /** [GET] /api/v1/comment/game/{gameId} 특정 게임에 달린 댓글 목록 */
  @GetMapping("/game/{gameId}")
  public ResponseEntity<ApiResponse> getCommentsByGame(
      @PathVariable Integer gameId) {
      List<Comment> comments = commentService.getCommentsByGameId(gameId);
    Map<String, Object> data = new HashMap<>();
      data.put("comments", comments);
      return ResponseEntity.ok(new ApiResponse("comment_list inquiry_success", data));
  }

  /** [PUT] /api/comment/{commentId} 댓글 수정 */
  @PutMapping("/{commentId}")
  public ResponseEntity<ApiResponse> updateComment(
      @PathVariable Integer commentId, @RequestBody CommentRequest request) {
      commentService.updateComment(commentId, request.getContent());
      Map<String, Object> data = null;
      return ResponseEntity.ok(new ApiResponse("update_comment_success", data));
  }

  /** [DELETE] /api/comment/{commentId} 댓글 삭제(softDelete 여부에 따라 분기) */
  @DeleteMapping("/{commentId}")
  public ResponseEntity<ApiResponse> deleteComment(
      @PathVariable Integer commentId, @RequestParam(defaultValue = "false") boolean softDelete) {
    commentService.deleteComment(commentId, softDelete);
      Map<String, Object> data = null;
      return ResponseEntity.ok(new ApiResponse("delete_comment_success", data));
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

