package store.warab.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.exception.BadRequestException;
import store.warab.common.util.ApiResponse;
import store.warab.dto.DiscordLinkRequestDto;
import store.warab.dto.DiscordLinkResponseDto;
import store.warab.dto.UserServerResponseDto;
import store.warab.service.AuthService;
import store.warab.service.DiscordLinkService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DiscordLinkController {
  private final DiscordLinkService discordLinkService;
  private final AuthService authService;

  // private final DiscordService discordService;

  @PostMapping("/games/{game_id}/server")
  public ResponseEntity<ApiResponse> createDiscordLink(
      @PathVariable("game_id") Long gameId,
      @Valid @RequestBody DiscordLinkRequestDto request,
      @CookieValue("jwt") String token) {
    try {
      Long userId = authService.extractUserId(token);

      discordLinkService.createDiscordLink(userId, gameId, request);
      return ResponseEntity.ok(new ApiResponse("server_create_success", null));
    } catch (BadRequestException e) {
      e.printStackTrace();
      return ResponseEntity.status(400).body(new ApiResponse("요청 오류: " + e.getMessage(), null));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(new ApiResponse("서버 오류: " + e.getMessage(), null));
    }
  }

  @GetMapping("/games/{game_id}/server")
  public ResponseEntity<ApiResponse> getDiscordLinksByGameId(@PathVariable("game_id") Long gameId) {
    try {
      List<DiscordLinkResponseDto> servers = discordLinkService.getDiscordLinksByGameId(gameId);
      return ResponseEntity.ok(
          new ApiResponse("discord_server_list inquiry_success", Map.of("servers", servers)));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(new ApiResponse("서버 오류: " + e.getMessage(), null));
    }
  }

  @GetMapping("/users/server")
  public ResponseEntity<ApiResponse> getDiscordLinksByUserId(@CookieValue("jwt") String token) {
    try {
      Long userId = authService.extractUserId(token);
      List<UserServerResponseDto> servers = discordLinkService.getUserServers(userId);
      return ResponseEntity.ok(
          new ApiResponse("user_server_list inquiry_success", Map.of("servers", servers)));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(new ApiResponse("서버 오류: " + e.getMessage(), null));
    }
  }

  @DeleteMapping("/games/{game_id}/server/{server_id}")
  public ResponseEntity<ApiResponse> deleteDiscordLink(
      @PathVariable("game_id") Long gameId,
      @PathVariable("server_id") Long serverId,
      @CookieValue("jwt") String token) {
    try {
      Long userId = authService.extractUserId(token);
      discordLinkService.deleteDiscordLink(userId, gameId, serverId);
      return ResponseEntity.ok(new ApiResponse("delete_server_success", null));
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.status(500).body(new ApiResponse("서버 오류: " + e.getMessage(), null));
    }
  }

  // 디스코드 링크 유효성 확인
  @GetMapping("/check_discord_link")
  public ResponseEntity<Map<String, Object>> checkDiscordLink(
      @RequestParam(name = "discord_link") String discordLink) {
    try {
      boolean isDuplicated = discordLinkService.isDiscordLinkDuplicated(discordLink);
      boolean isValid = discordLinkService.isValidDiscordLink(discordLink);

      Map<String, Object> response = new HashMap<>();

      if (isDuplicated) {
        response.put("message", "already_exist_discordlink");
        response.put("duplication", true);
      }
      if (isValid) {
        response.put("message", "available_discordlink");
        response.put("duplication", false);
      }

      return ResponseEntity.ok(response);
    } catch (Exception e) {
      e.printStackTrace();
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("message", "server_error");
      errorResponse.put("error", e.getMessage());
      return ResponseEntity.status(500).body(errorResponse);
    }
  }
}
