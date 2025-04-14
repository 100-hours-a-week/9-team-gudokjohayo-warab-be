package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import store.warab.entity.DiscordLink;

@Getter
@AllArgsConstructor
public class UserServerResponseDto {

  @JsonProperty("server_id")
  private Long serverId;

  @JsonProperty("discord_url")
  private String discordUrl;

  @JsonProperty("game_name")
  private String gameName;

  @JsonProperty("game_id")
  private Long gameId;

  @JsonProperty("created_at")
  private LocalDate createdAt;

  @JsonProperty("expires_at")
  private LocalDate expiresAt;

  public static UserServerResponseDto fromEntity(DiscordLink discordLink) {
    if (discordLink == null) return null;

    return new UserServerResponseDto(
        discordLink.getId(),
        discordLink.getDiscordUrl(),
        discordLink.getGame() != null ? discordLink.getGame().getTitle() : null,
        discordLink.getGame() != null ? discordLink.getGame().getId() : null,
        discordLink.getCreatedAt().toLocalDate(),
        discordLink.getExpiredAt());
  }
}
