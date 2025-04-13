package store.warab.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import store.warab.entity.DiscordLink;

@Getter
@AllArgsConstructor
public class DiscordLinkResponseDto {
  @JsonProperty("server_id")
  private Long id;

  @JsonProperty("user_id")
  private Long userId;

  @JsonProperty("url")
  private String discordUrl;

  @JsonProperty("name")
  private String channelName;

  @JsonProperty("description")
  private String channelDescription;

  @JsonProperty("member_count")
  private Integer memberCount;

  @JsonProperty("icon_url")
  private String channelIcon;

  @JsonProperty("created_at")
  private LocalDateTime createdAt;

  @JsonProperty("expires_at")
  private LocalDate expiredAt;

  public static DiscordLinkResponseDto fromEntity(DiscordLink discordLink) {
    if (discordLink == null) {
      return null;
    }

    return new DiscordLinkResponseDto(
        discordLink.getId(),
        discordLink.getUser() != null ? discordLink.getUser().getId() : null,
        discordLink.getDiscordUrl(),
        discordLink.getChannelName(),
        discordLink.getChannelDescription(),
        discordLink.getMemberCount(),
        discordLink.getChannelIcon(),
        discordLink.getCreatedAt(),
        discordLink.getExpiredAt());
  }
}
