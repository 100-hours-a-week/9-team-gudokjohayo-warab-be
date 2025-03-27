package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import store.warab.entity.Comment;
import store.warab.entity.User;

import java.time.LocalDateTime;


public class CommentResponseDto {
    @JsonProperty("comment_id")
    private Long commentId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("name")
    private String nickName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("deleted_at")
    private LocalDateTime deletedAt;

    public CommentResponseDto(User user, Comment comment) {
        this.commentId = comment.getCommentId();
        this.userId = user.getId();
        this.nickName = user.getNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.deletedAt = comment.getDeletedAt();
    }
}
