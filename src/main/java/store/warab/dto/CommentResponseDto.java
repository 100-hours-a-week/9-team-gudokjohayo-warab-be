package store.warab.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.LocalDateTime;
import store.warab.entity.Comment;
import store.warab.entity.User;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentResponseDto {
  private Long commentId;
  private Long userId;

  //  @JsonProperty("user_discord")
  //  private String userDiscord;

  @JsonProperty
  private String nickName;
  private String content;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private LocalDateTime deletedAt;

  public CommentResponseDto(User user, Comment comment) {
    this.commentId = comment.getCommentId();
    this.userId = user.getId();
    // this.userDiscord = user.getDiscordLink();
    this.nickName = user.getNickname();
    this.content = comment.getContent();
    this.createdAt = comment.getCreatedAt();
    this.updatedAt = comment.getUpdatedAt();
    this.deletedAt = comment.getDeletedAt();
  }
}
