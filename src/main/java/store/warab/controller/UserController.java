package store.warab.controller;

// import java.lang.classfile.constantpool.StringEntry;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.util.ApiResponse;
import store.warab.dto.UserDto;
import store.warab.dto.UserUpdateResponseDto;
import store.warab.service.AuthService;
import store.warab.service.DiscordService;
import store.warab.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;
  private final AuthService authService;
  private final DiscordService discordService;

  public UserController(UserService userService, AuthService authService, DiscordService discordService) {

    this.userService = userService;
    this.authService = authService;
    this.discordService = discordService;
  }

  // 유저 프로필 조회
  @GetMapping("/profile/{userId}")
  public ResponseEntity<ApiResponse> getProfile(@PathVariable long userId) {
    UserDto userDto = userService.getUserById(userId);
    return ResponseEntity.ok(new ApiResponse("user_profile_inquiry_success", userDto));
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
  public ResponseEntity<?> checkDiscordLink(@RequestParam(required = true) String discordLink) {
    boolean isDuplicated = discordService.validateDiscordLink(discordLink, userService.isDiscordLinkDuplicated(discordLink));
    if (isDuplicated) {
      return ResponseEntity.ok(
          Map.of("message", "already_exist_discord_link", "duplication", isDuplicated));
    }

    return ResponseEntity.ok(
        Map.of("message", "available_discord_link", "duplication", isDuplicated));
  }

  // 회원 정보 수정
  @PatchMapping("/profile")
  public ResponseEntity<ApiResponse> updateProfile(
      @RequestBody UserUpdateResponseDto dto, @RequestHeader("Authorization") String token) {

    Long tokenUserId = authService.extractUserId(token);
    authService.verifyUser(tokenUserId, dto.getId());

    userService.updateUserInfo(dto);
    return ResponseEntity.ok(new ApiResponse("update_user_data_success", null));
  }
}
