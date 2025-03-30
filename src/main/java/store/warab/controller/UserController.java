package store.warab.controller;

// import java.lang.classfile.constantpool.StringEntry;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import store.warab.common.util.ApiResponse;
import store.warab.dto.UserProfileResponseDto;
import store.warab.dto.UserProfileUpdateRequest;
import store.warab.service.AuthService;
import store.warab.service.DiscordService;
import store.warab.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;
  private final AuthService authService;
  private final DiscordService discordService;

  public UserController(
      UserService userService, AuthService authService, DiscordService discordService) {

    this.userService = userService;
    this.authService = authService;
    this.discordService = discordService;
  }

  // 로그인 구현 되면 다른 사람이 못들어오게 해야함
  // /api/v1/users/profile
  @GetMapping("/profile")
  public ResponseEntity<ApiResponse> getProfile(@CookieValue("jwt") String token) {
    Long tokenUserId = authService.extractUserId(token);
    UserProfileResponseDto userProfileResponseDto = userService.getUserById(tokenUserId);

    return ResponseEntity.ok(
        new ApiResponse("user_profile_inquiry_success", userProfileResponseDto));
  }

  // 닉네임 중복 확인
  @GetMapping("/check_nickname")
  public ResponseEntity<?> checkNickname(@RequestParam(required = true) String nickname) {
    boolean isDuplicated = userService.isNicknameDuplicated(nickname);
    if (isDuplicated) {
      return ResponseEntity.ok(
          Map.of("message", "already_exist_nickname", "duplication", isDuplicated));
    }

    return ResponseEntity.ok(Map.of("message", "available_nickname", "duplication", isDuplicated));
  }

  // 디스코드 링크 유효성 확인
  @GetMapping("/check_discord_link")
  public ResponseEntity<?> checkDiscordLink(
      @RequestParam(required = true, name = "discord_link") String discordLink) {
    boolean isDuplicated = userService.isDiscordLinkDuplicated(discordLink);
    try {
      discordService.validateDiscordLink(discordLink, isDuplicated);
      return ResponseEntity.ok(Map.of("message", "available_discordLink", "duplication", false));
    } catch (ResponseStatusException e) {
      if (isDuplicated) {
        return ResponseEntity.ok(
            Map.of("message", "already_exist_discordLink", "duplication", true));
      }
      return ResponseEntity.status(e.getStatusCode())
          .body(Map.of("message", "invalid_discordlink", "error", e.getMessage()));
    }
  }

  // 회원 정보 수정
  //    /api/v1/users/profile
  @PutMapping("/profile")
  public ResponseEntity<ApiResponse> updateProfile(
      @Valid @RequestBody UserProfileUpdateRequest dto, @CookieValue("jwt") String token) {
    Long tokenUserId = authService.extractUserId(token);

    userService.updateUserInfo(dto, tokenUserId);
    return ResponseEntity.ok(new ApiResponse("update_user_data_success", null));
  }
}
