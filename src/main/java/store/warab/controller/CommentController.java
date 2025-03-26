package store.warab.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.util.ApiResponse;
import store.warab.dto.CommentRequest;
import store.warab.entity.Comment;
import store.warab.service.AuthService;
import store.warab.service.CommentService;

@RestController
@RequestMapping("/api/v1/games") // URL
// @CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

  private final CommentService commentService;
  private final AuthService authService;

  public CommentController(CommentService commentService, AuthService authService) {

    this.commentService = commentService;
    this.authService = authService;
  }

  /** [POST] /api/v1/games/{gameId}/comment 새 댓글 생성 */
  @PostMapping("/{gameId}/comment")
  public ResponseEntity<ApiResponse> createComment(
      @CookieValue("jwt") String token,
      @PathVariable Long gameId,
      @RequestBody CommentRequest request) {
    Long tokenUserId = authService.extractUserId(token);
    commentService.createComment(tokenUserId, gameId, request.getContent());
    Map<String, Object> data = null;
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(new ApiResponse("comment_create_success", data));
  }

  /** [GET] /api/v1/games/{gameId}/comment 특정 게임에 달린 댓글 목록 */
  @GetMapping("/{gameId}/comment")
  public ResponseEntity<ApiResponse> getCommentsByGame(@PathVariable Integer gameId) {
    List<Comment> comments = commentService.getCommentsByGameId(gameId);
    Map<String, Object> data = new HashMap<>();
    data.put("comments", comments);
    return ResponseEntity.ok(new ApiResponse("comment_list inquiry_success", data));
  }

  /** [PUT] api/v1/games/{gameId}/comment/{commentId} 댓글 수정 */
  @PutMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse> updateComment(
      @PathVariable Integer commentId,
      @RequestBody CommentRequest request,
      @CookieValue("jwt") String token) {
    Long tokenUserId = authService.extractUserId(token);
    boolean isOwner = commentService.isOwnerOfComment(tokenUserId, commentId);
    if (!isOwner) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ApiResponse("required_permission", null));
    }
    commentService.updateComment(commentId, request.getContent());
    Map<String, Object> data = null;
    return ResponseEntity.ok(new ApiResponse("update_comment_success", data));
  }

  /** [DELETE] api/v1/games/{gameId}/comment/{commentId} 댓글 삭제(softDelete 여부에 따라 분기) */
  @DeleteMapping("/comment/{commentId}")
  public ResponseEntity<ApiResponse> deleteComment(
      @PathVariable Integer commentId,
      @RequestParam(defaultValue = "false") boolean softDelete,
      @CookieValue("jwt") String token) {

    Long tokenUserId = authService.extractUserId(token);
    boolean isOwner = commentService.isOwnerOfComment(tokenUserId, commentId);
    if (!isOwner) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN)
          .body(new ApiResponse("required_permission", null));
    }
    commentService.deleteComment(commentId, tokenUserId, softDelete);
    Map<String, Object> data = null;
    return ResponseEntity.ok(new ApiResponse("delete_comment_success", data));
  }
}
