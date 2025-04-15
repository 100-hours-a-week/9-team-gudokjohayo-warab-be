package store.warab.controller;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.exception.ForbiddenException;
import store.warab.common.util.ApiResponse;
import store.warab.dto.UserProfileResponseDto;
import store.warab.dto.UserProfileUpdateRequest;
import store.warab.service.AuthService;
import store.warab.service.UserService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private final UserService userService;
  private final AuthService authService;

  public UserController(UserService userService, AuthService authService) {
    this.userService = userService;
    this.authService = authService;
  }

  // 로그인 구현 되면 다른 사람이 못들어오게 해야함
  // /api/v1/users/profile
  @GetMapping("/profile")
  public ResponseEntity<ApiResponse> getProfile(
      @CookieValue(value = "jwt", required = false) String token) {
    Long userId = null;
    if (authService.isValid(token)) {
      userId = authService.extractUserId(token); // 유효하지 않으면 null
    } else throw new ForbiddenException("권한이 없습니다.");
    UserProfileResponseDto userProfileResponseDto = userService.getUserById(userId);

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
