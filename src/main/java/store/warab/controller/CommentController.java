package store.warab.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.entity.Comment;
import store.warab.service.CommentService;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games/{gameId}/comment")
@CrossOrigin(origins = "http://localhost:3000")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * [POST] /games/{gameId}/comment
     * 새 댓글 생성
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createComment(
            @PathVariable Long gameId,
            @RequestBody CommentRequest request) {
        commentService.createComment(request.getNickname(), gameId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Collections.singletonMap("message", "comment_create_success"));
    }

    /**
     * [GET] /games/{gameId}/comment
     * 특정 게임에 달린 댓글 목록
     */
    @GetMapping
    public List<Comment> getCommentsByGame(@PathVariable Long gameId) {
        return commentService.getCommentsByGameId(gameId);
    }

    /**
     * [PATCH] /games/{gameId}/comment/{commentId}
     * 댓글 수정
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long gameId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest request) {
        commentService.updateComment(gameId, commentId, request.getContent());
        return ResponseEntity.ok().build();
    }

    /**
     * [DELETE] /games/{gameId}/comment/{commentId}
     * 댓글 삭제(소프트 딜리트)
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long gameId,
            @PathVariable Long commentId) {
        commentService.deleteComment(gameId, commentId);
        return ResponseEntity.ok().build();
    }

    @Getter
    @Setter
    public static class CommentRequest {
        private String nickname;
        private String content;
    }
}
