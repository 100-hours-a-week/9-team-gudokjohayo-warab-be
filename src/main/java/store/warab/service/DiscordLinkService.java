package store.warab.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import store.warab.common.exception.BadRequestException;
import store.warab.common.exception.NotFoundException;
import store.warab.dto.DiscordInviteResponseDto;
import store.warab.dto.DiscordLinkRequestDto;
import store.warab.dto.DiscordLinkResponseDto;
import store.warab.dto.UserServerResponseDto;
import store.warab.entity.DiscordLink;
import store.warab.entity.GameStatic;
import store.warab.entity.User;
import store.warab.repository.DiscordLinkRepository;
import store.warab.repository.GameStaticRepository;
import store.warab.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class DiscordLinkService {
  private final DiscordLinkRepository discordLinkRepository;
  private final UserRepository userRepository;
  private final GameStaticRepository gameStaticRepository;
  private final DiscordService discordService;
  private final RestTemplate restTemplate = new RestTemplate();
  private static final String DISCORD_API_URL = "https://discord.com/api/v10/invites/";

  @Transactional
  public void createDiscordLink(Long userId, Long gameId, DiscordLinkRequestDto request) {
    try {
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다: " + userId));

      GameStatic game =
          gameStaticRepository
              .findById(gameId)
              .orElseThrow(() -> new NotFoundException("게임을 찾을 수 없습니다: " + gameId));

      if (discordLinkRepository.existsByUserIdAndGameIdAndDeletedAtIsNull(userId, gameId)) {
        throw new BadRequestException("이미 등록된 디스코드 링크입니다.");
      }

      // 디스코드 링크 유효성 검사
      try {
        discordService.validateDiscordLink(request.getDiscordUrl());
      } catch (Exception e) {
        e.printStackTrace();
        throw new BadRequestException("디스코드 링크 유효성 검사 실패: " + e.getMessage());
      }

      // 디스코드 초대 링크 정보 조회
      String inviteCode = discordService.extractInviteCode(request.getDiscordUrl());
      DiscordInviteResponseDto inviteInfo;
      try {
        inviteInfo = discordService.getDiscordInviteInfo(inviteCode);
      } catch (Exception e) {
        e.printStackTrace();
        throw new BadRequestException("디스코드 초대 정보 조회 실패: " + e.getMessage());
      }

      // 아이콘 URL 생성
      String iconUrl = null;
      if (inviteInfo.getGuild().getIcon() != null && !inviteInfo.getGuild().getIcon().isEmpty()) {
        iconUrl =
            String.format(
                "https://cdn.discordapp.com/icons/%s/%s.png",
                inviteInfo.getGuild().getId(), inviteInfo.getGuild().getIcon());
      }

      DiscordLink discordLink =
          DiscordLink.builder()
              .user(user)
              .game(game)
              .discordUrl(request.getDiscordUrl())
              .expiredAt(inviteInfo.getExpiresAt())
              .channelName(inviteInfo.getGuild().getName())
              .channelDescription(inviteInfo.getGuild().getDescription())
              .memberCount(inviteInfo.getApproximateMemberCount())
              .channelIcon(iconUrl)
              .build();

      discordLinkRepository.save(discordLink);
    } catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
  }

  public List<DiscordLinkResponseDto> getDiscordLinksByGameId(Long gameId) {
    List<DiscordLink> discordLinks = discordLinkRepository.findByGameIdAndDeletedAtIsNull(gameId);
    return discordLinks.stream()
        .map(DiscordLinkResponseDto::fromEntity)
        .collect(Collectors.toList());
  }

  public List<DiscordLinkResponseDto> getDiscordLinksByUserId(Long userId) {
    List<DiscordLink> discordLinks = discordLinkRepository.findByUserIdAndDeletedAtIsNull(userId);
    return discordLinks.stream()
        .map(DiscordLinkResponseDto::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional
  public void deleteDiscordLink(Long userId, Long gameId, Long serverId) {
    DiscordLink discordLink =
        discordLinkRepository
            .findById(serverId)
            .orElseThrow(() -> new NotFoundException("디스코드 링크를 찾을 수 없습니다."));

    if (!discordLink.getUser().getId().equals(userId)
        || !discordLink.getGame().getId().equals(gameId)) {
      throw new BadRequestException("삭제 권한이 없습니다.");
    }

    // 소프트 딜리트: deletedAt 필드 설정
    discordLink.setDeletedAt(LocalDateTime.now());
    discordLinkRepository.save(discordLink);
  }

  /**
   * 디스코드 링크 중복 검사
   *
   * @param discordUrl 디스코드 초대 링크
   * @return 중복 여부
   */
  public boolean isDiscordLinkDuplicated(String discordUrl) {
    return discordLinkRepository.existsByDiscordUrlAndDeletedAtIsNull(discordUrl);
  }

  public boolean isValidDiscordLink(String discordUrl) {
    try {
      String inviteCode = extractInviteCode(discordUrl);
      String apiUrl = DISCORD_API_URL + inviteCode + "?with_counts=true";
      restTemplate.getForObject(apiUrl, Object.class);
      return true;
    } catch (HttpClientErrorException e) {
      return false;
    } catch (Exception e) {
      return false;
    }
  }

  public List<UserServerResponseDto> getUserServers(Long userId) {
      List<DiscordLink> discordLinks = discordLinkRepository.findByUserIdAndDeletedAtIsNull(userId);
      return discordLinks.stream()
          .map(UserServerResponseDto::fromEntity)
          .collect(Collectors.toList());
  }

  private String extractInviteCode(String discordUrl) {
    // https://discord.gg/{inviteCode} 형식에서 inviteCode 추출
    String[] parts = discordUrl.split("/");
    return parts[parts.length - 1];
  }
}
