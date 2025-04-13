package store.warab.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import store.warab.common.exception.BadRequestException;
import store.warab.common.exception.InternalServerException;
import store.warab.dto.DiscordInviteResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordService {

  private final RestTemplate restTemplate;

  // 초대 코드 추출 메서드
  public String extractInviteCode(String discordLink) {
    if (discordLink == null || discordLink.isEmpty()) {
      return null;
    }

    Pattern pattern = Pattern.compile("(?:discord\\.gg|discord\\.com/invite)/([a-zA-Z0-9]+)");
    Matcher matcher = pattern.matcher(discordLink);

    if (matcher.find()) {
      return matcher.group(1);
    }
    return null;
  }

  // 초대 코드 유효성 검사 메서드
  public boolean isValidInviteLink(String inviteCode) {
    String url = "https://discord.com/api/v10/invites/" + inviteCode;

    try {
      ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.GET, null, void.class);
      return response.getStatusCode().is2xxSuccessful();
    } catch (HttpClientErrorException.NotFound e) {
      return false; // 초대 코드가 존재하지 않음
    } catch (Exception e) {
      log.error("Discord 초대 링크 확인 중 오류 발생", e);
      throw new InternalServerException("Discord API 호출 중 오류 발생");
    }
  }

  // 디스코드 링크 검증 및 중복 검사
  public void validateDiscordLink(String discordLink) {
    String inviteCode = extractInviteCode(discordLink);

    if (inviteCode == null) {
      throw new BadRequestException("올바른 디스코드 초대 링크 형식이 아닙니다.");
    }

    try {
      if (!isValidInviteLink(inviteCode)) {
        throw new BadRequestException("유효하지 않은 디스코드 초대 링크입니다.");
      }
    } catch (ResponseStatusException e) {
      if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
        throw e;
      }
      throw new BadRequestException("유효하지 않은 디스코드 초대 링크입니다.");
    }
  }

  // 디스코드 초대 링크 정보 조회
  public DiscordInviteResponseDto getDiscordInviteInfo(String inviteCode) {
    String url = "https://discord.com/api/v10/invites/" + inviteCode + "?with_counts=true";

    try {
      ResponseEntity<DiscordInviteResponseDto> response =
          restTemplate.exchange(url, HttpMethod.GET, null, DiscordInviteResponseDto.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      throw new BadRequestException("유효하지 않은 디스코드 초대 링크입니다.");
    } catch (HttpClientErrorException.NotFound e) {
      throw new BadRequestException("존재하지 않는 디스코드 초대 링크입니다.");
    } catch (Exception e) {
      log.error("Discord API 호출 중 오류 발생", e);
      throw new InternalServerException("Discord API 호출 중 오류 발생");
    }
  }
}
