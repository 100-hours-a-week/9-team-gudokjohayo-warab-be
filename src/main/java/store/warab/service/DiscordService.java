package store.warab.service;

import discord4j.core.DiscordClient;
import discord4j.discordjson.json.InviteData;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class DiscordService {

  @Value("${discord.bot.token}")
  private String botToken;

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
    if (botToken == null || botToken.isEmpty()) {
      log.error("Discord bot token이 설정되지 않았습니다.");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Discord 서비스 설정 오류");
    }

    try {
      log.info("DiscordClient 생성 시작 - botToken: {}", botToken);
      DiscordClient client = DiscordClient.create(botToken);
      log.info("Discord 초대 링크 검증 중 - 초대 코드: {}", inviteCode);
      InviteData inviteData = client.getInvite(inviteCode).block();
      return inviteData != null;
    } catch (Exception e) {
      log.error("Discord 초대 링크 검증 중 오류 발생: {}", e.getMessage(), e);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Discord API 호출 중 오류 발생");
    }
  }

  // 디스코드 링크 검증 및 중복 검사
  public void validateDiscordLink(String discordLink, boolean isDuplicate) {
    String inviteCode = extractInviteCode(discordLink);

    if (inviteCode == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "올바른 디스코드 초대 링크 형식이 아닙니다.");
    }

    try {
      if (!isValidInviteLink(inviteCode)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 디스코드 초대 링크입니다.");
      }
    } catch (ResponseStatusException e) {
      if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
        throw e;
      }
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "유효하지 않은 디스코드 초대 링크입니다.");
    }

    if (isDuplicate) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 디스코드 링크입니다.");
    }
  }
}
